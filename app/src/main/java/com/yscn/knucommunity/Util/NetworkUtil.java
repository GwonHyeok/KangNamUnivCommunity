package com.yscn.knucommunity.Util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.yscn.knucommunity.Items.CommentListItems;
import com.yscn.knucommunity.Items.DefaultBoardListItems;
import com.yscn.knucommunity.Items.LibrarySeatItems;
import com.yscn.knucommunity.Items.MajorDetailItems;
import com.yscn.knucommunity.Items.MajorSimpleListItems;
import com.yscn.knucommunity.Items.SchoolRestrauntItems;
import com.yscn.knucommunity.Items.StudentCouncilListItems;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by GwonHyeok on 14. 11. 19..
 */
public class NetworkUtil {
    public static enum SchoolRestraunt {SHAL, GYUNG, GISUK, INSA}

    public static enum LoginStatus {FAIL, NOMEMBER, SUCCESS, HASMEMBER}

    public static enum BoardType {FREE, FAQ, GREENLIGHT}

    private static NetworkUtil instance;

    private HttpClient httpClient = new DefaultHttpClient();

    private NetworkUtil() {

    }

    public static NetworkUtil getInstance() {
        if (instance == null) {
            instance = new NetworkUtil();
        }
        return instance;
    }


    public LoginStatus RegisterAppServer(String studentnumber, String nickname, String name) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        ContentType contentType = ContentType.create("text/plain", Charset.forName("UTF-8"));
        String defaultStorage = Environment.getExternalStorageDirectory().getPath();
        String temporaryFilePath = defaultStorage + "/" + "temp_knucommunity_profileimage.png";
        File profileImageFile = new File(temporaryFilePath);

        Bitmap bitmap = UserData.getInstance().getUserProfile();
        FileOutputStream fileOutputStream = new FileOutputStream(profileImageFile);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        byte[] bitmapdata = byteArrayOutputStream.toByteArray();
        fileOutputStream.write(bitmapdata);
        fileOutputStream.flush();

        HttpPost httpPost = new HttpPost(UrlList.APP_REGISTER_URL);
        HttpEntity entity = MultipartEntityBuilder.create()
                .addTextBody("studentnumber", studentnumber, contentType)
                .addTextBody("nickname", nickname, contentType)
                .addTextBody("name", name, contentType)
                .addBinaryBody("userfile", profileImageFile)
                .build();
        httpPost.setEntity(entity);
        HttpResponse httpResponse = httpClient.execute(httpPost);

        byteArrayOutputStream.close();
        fileOutputStream.close();

        if (profileImageFile.delete()) {
            Log.d(getClass().getSimpleName(), "Delete Temporary ImageFile");
            UserData.getInstance().setUserProfile(null);
        }

        InputStreamReader inputStreamReader =
                new InputStreamReader(httpResponse.getEntity().getContent());
        JSONObject resultObject = (JSONObject) jsonParser.parse(inputStreamReader);
        if (checkResultData(resultObject)) {
            String status = resultObject.get("status").toString();
            if (status.equals("hasmember")) {
                /* 이미 회원이 있으나 회원가입을 요청 했을때 */
                return LoginStatus.HASMEMBER;
            } else if (status.equals("registersuccess")) {
                /* 회원 가입 성공 */
                return LoginStatus.SUCCESS;
            }
        } else {
            /* 서버의 문제가 있을때 */
            return LoginStatus.FAIL;
        }
        /* 그외의 예상 하지 못한 오류때문에 */
        return LoginStatus.FAIL;
    }

    public LoginStatus LoginAppServer(String studentnumber, String password) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        HashMap<String, String> parameters = new HashMap<String, String>();
        String studentname = "";
        parameters.put("user_id", studentnumber);
        parameters.put("user_pwd", password);
        HttpResponse httpResponse = postData(UrlList.SCHOLL_SERVER_LOGIN_URL, parameters);
        JSONObject loginjsonObject = (JSONObject) jsonParser.parse(
                new InputStreamReader(httpResponse.getEntity().getContent()));
        String result = loginjsonObject.get("result").toString();

        /*
         * 학교 서버 로그인에 성공함
         * 앱서버 로그인 확인
         */
        if (result.equals("success")) {
            Header[] headers = httpResponse.getHeaders("Set-Cookie");
            for (Header header : headers) {
                if (header.getValue().split("=")[0].equals("mast_name_e")) {
                    String name = header.getValue().split(";")[0];
                    name = name.replace("mast_name_e=", "").replace("\"", "");
                    byte[] bytes = Base64.decode(name, 0);
                    studentname = URLDecoder.decode(new String(bytes, "UTF-8"), "UTF-8");
                }
            }

            /* 앱서버 post 파라미터 생성 */
            HashMap<String, String> appLoginParameters = getTextParameters(
                    new String[]{"studentnumber"}, new String[]{studentnumber});
            httpResponse = postData(UrlList.APP_LOGIN_URL, appLoginParameters);
            JSONObject apploginjsonObject = (JSONObject) jsonParser.parse(
                    new InputStreamReader(httpResponse.getEntity().getContent()));
            /* 서버 Response 확인 */
            if (checkResultData(apploginjsonObject)) {
                /* 로그인 작업시 나온 이름, 학번정보를 메모리 상에 올려놓음 */
                UserData.getInstance().setStudentNumber(studentnumber);
                UserData.getInstance().setStudentName(studentname);
                log(apploginjsonObject.toJSONString());
                log("Student Name : " + studentname);
                result = apploginjsonObject.get("status").toString();
                /*
                 * 앱 서버에 멤버가 있을 경우 Token 발급
                 * 없는 경우에 Nomember 리턴
                 */
                if (result.equals("hasmember")) {
                    /* 멤버가 있을경우 토큰을 메모리에 올려놓고 Preference에 저장 */
                    String token = apploginjsonObject.get("token").toString();
                    UserData.getInstance().setUserToken(token);
                    log("Token  : " + token);

                    log("Save User Data");
                    UserDataPreference preference = new UserDataPreference(ApplicationContextProvider.getContext());
                    preference.removeAll();
                    preference.setStudentNumber(studentnumber);
                    preference.setToken(token);
                    preference.setStudentName(studentname);
                    return LoginStatus.SUCCESS;
                } else if (result.equals("nomember")) {
                    return LoginStatus.NOMEMBER;
                } else {
                    /* 예상 하지 못한 다른 경우에서 로그인 실패 */
                    return LoginStatus.FAIL;
                }
            } else {
                /* 앱 서버 로그인시 Respone 에 문제가 있을 경우 */
                return LoginStatus.FAIL;
            }
        } else {
            /* 학교 서버 로그인 실페 */
            return LoginStatus.FAIL;
        }
    }

    public ArrayList<MajorDetailItems> getMajorDetailInfo(int majorType) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        ArrayList<MajorDetailItems> itemses = new ArrayList<MajorDetailItems>();
        HttpResponse httpResponse = postData(UrlList.PROFESSOR_GET_DETAIL_INFO + String.valueOf(majorType), null);
        JSONObject jsonObject = (JSONObject) jsonParser.parse(
                new InputStreamReader(httpResponse.getEntity().getContent()));
        if (checkResultData(jsonObject)) {
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");
            for (Object dataObject : jsonArray) {
                JSONObject dataJsonObject = (JSONObject) dataObject;
                String name = URLDecode(dataJsonObject.get("name").toString());
                String major = URLDecode(dataJsonObject.get("major").toString());
                String phoneNumber = URLDecode(dataJsonObject.get("phone").toString());
                String email = URLDecode(dataJsonObject.get("email").toString());
                itemses.add(new MajorDetailItems(name, major, phoneNumber, email));
            }
        } else {
            return null;
        }
        return itemses;
    }

    public ArrayList<SchoolRestrauntItems> getRestrauntInfo(SchoolRestraunt restraunt) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        ArrayList<SchoolRestrauntItems> itemses = new ArrayList<>();
        HttpResponse httpResponse = postData(UrlList.SCHOOL_RESTRAUNT_INFO + restraunt.name().toLowerCase(), null);
        JSONObject jsonObject = (JSONObject) jsonParser.parse(
                new InputStreamReader(httpResponse.getEntity().getContent()));
        if (checkResultData(jsonObject)) {
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");
            for (Object object : jsonArray) {
                JSONObject object1 = (JSONObject) object;
                String foodName = URLDecode(object1.get("foodname").toString());
                String foodPrice = URLDecode(object1.get("foodprice").toString());
                itemses.add(new SchoolRestrauntItems(foodName, foodPrice));
            }
        }
        return itemses;
    }

    public ArrayList<LibrarySeatItems> getLibrarySeatInfo() throws IOException {
        ArrayList<LibrarySeatItems> itemses = new ArrayList<LibrarySeatItems>();
        URL url = new URL(UrlList.LIBRARY_SEAT_URL);
        Document document = Jsoup.parse(url, 10000);
        Elements elements = document.getElementsByTag("tr");
        /*
            (0 || 1 || 2) 은 무시할 정보 -- for 문 index 를 2부터
         */
        for (int i = 3; i < elements.size(); i++) {
            Elements innerElements = elements.get(i).getElementsByTag("td");
            int total = parseInt(innerElements.get(2).text());
            int useSeat = parseInt(innerElements.get(3).text());
            int emptySeat = total - useSeat;
            itemses.add(new LibrarySeatItems(total, useSeat, emptySeat));
        }
        return itemses;
    }

    public ArrayList<MajorSimpleListItems> getMajorSimpleInfo() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        ArrayList<MajorSimpleListItems> itemses = new ArrayList<MajorSimpleListItems>();
        HttpResponse httpResponse = postData(UrlList.MAJOR_GET_SIMPLE_INFO, null);
        JSONObject jsonObject = (JSONObject) jsonParser.parse(
                new InputStreamReader(httpResponse.getEntity().getContent()));
        if (checkResultData(jsonObject)) {
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");
            for (Object object : jsonArray) {
                JSONObject jObject = (JSONObject) object;
                String majorType = URLDecoder.decode(jObject.get("majorType").toString(), "UTF-8");
                String majorName = URLDecoder.decode(jObject.get("majorName").toString(), "UTF-8");
                String majorHomepage = URLDecoder.decode(jObject.get("majorHomepage").toString(), "UTF-8");
                itemses.add(new MajorSimpleListItems(majorName, majorHomepage, majorType));
            }
        }
        return itemses;
    }

    public HashMap<String, ArrayList<StudentCouncilListItems>> getCouncilInfo() throws IOException, ParseException {
        HashMap<String, ArrayList<StudentCouncilListItems>> dataMap = new HashMap<String, ArrayList<StudentCouncilListItems>>();
        ArrayList<StudentCouncilListItems> riffleItem = new ArrayList<StudentCouncilListItems>();
        ArrayList<StudentCouncilListItems> dragItem = new ArrayList<StudentCouncilListItems>();
        HttpResponse httpResponse = postData(UrlList.STUDENT_GET_COUNCIL_INFO, null);
        JSONParser jsonParser = new JSONParser();
        JSONObject object = (JSONObject) jsonParser.parse(
                new InputStreamReader(httpResponse.getEntity().getContent()));

        if (!checkResultData(object)) {
            return null;
        }
        JSONArray jsonArray = (JSONArray) object.get("data");
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            String type = jsonObject.get("type").toString();
            String title = jsonObject.get("title").toString();
            String message = jsonObject.get("message").toString();

            if (type.equals("riffle")) {
                riffleItem.add(new StudentCouncilListItems(title, message));
            } else if (type.equals("drag")) {
                dragItem.add(new StudentCouncilListItems(title, message));
            }
        }
        dataMap.put("riffle", riffleItem);
        dataMap.put("drag", dragItem);
        return dataMap;
    }

    public ArrayList<DefaultBoardListItems> getDefaultboardList(BoardType boardType, int page) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        HttpResponse httpResponse = null;

        if (boardType == BoardType.FREE) {
            httpResponse = postData(UrlList.FREEBOARD_GET_LIST + page, null);
        } else if (boardType == BoardType.FAQ) {
            httpResponse = postData(UrlList.FAQBOARD_GET_LIST + page, null);
        } else if (boardType == BoardType.GREENLIGHT) {
            httpResponse = postData(UrlList.GREENLIGHT_GET_LIST + page, null);
        } else {
            return null;
        }

        JSONObject object = (JSONObject) jsonParser.parse(
                new InputStreamReader(httpResponse.getEntity().getContent()));
        if (!checkResultData(object)) {
            return null;
        }
        ArrayList<DefaultBoardListItems> list = new ArrayList<>();
        JSONArray jsonArray = (JSONArray) object.get("data");
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            String studentnumber = jsonObject.get("studentnumber").toString();
            String title = jsonObject.get("title").toString();
            String contentid = jsonObject.get("contentid").toString();
            String writername = jsonObject.get("writername").toString();
            String time = jsonObject.get("time").toString();
            String commentsize = jsonObject.get("commentsize").toString();
            list.add(new DefaultBoardListItems(title, studentnumber, contentid, writername, time, Integer.parseInt(commentsize)));
        }
        return list;
    }

    public String getDefaultboardContent(String contentID) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        HttpResponse httpResponse = postData(UrlList.DEFAULTBOARD_GET_CONTENT + contentID, null);
        JSONObject object = (JSONObject) jsonParser.parse(
                new InputStreamReader(httpResponse.getEntity().getContent()));
        if (!checkResultData(object)) {
            return null;
        }
        return object.get("content").toString();
    }

    /**
     * @param contentID greenLight Board Content ID
     * @return HashMap <String String>
     * 그린라이트 결과에 대해서 해쉬맵 형태로 넘겨준다.
     * positivesize : 그린라이트 켬 사이즈.
     * negativesize : 그린라이트 끔 사이즈.
     * isChecked : 그 사람이 그린라이트를 체크 했었는지 확인.
     * @throws IOException
     * @throws ParseException
     */
    public HashMap<String, String> getGreenLightResult(String contentID) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("studentnumber", UserData.getInstance().getStudentNumber());
        HttpResponse httpResponse = postData(UrlList.GET_GREENLIGHT_RESULT + contentID, parameter);
        JSONObject object = (JSONObject) jsonParser.parse(
                new InputStreamReader(httpResponse.getEntity().getContent()));
        if (!checkResultData(object)) {
            return null;
        }
        HashMap<String, String> greenLightData = new HashMap<>();
        greenLightData.put("positivesize", object.get("positivesize").toString());
        greenLightData.put("negativesize", object.get("negativesize").toString());
        greenLightData.put("isChecked", object.get("isChecked").toString());
        return greenLightData;
    }


    public HashMap<String, String> setGreenLightResult(String contentID, boolean isOn) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("studentnumber", UserData.getInstance().getStudentNumber());
        parameter.put("result", isOn ? "1" : "0");
        HttpResponse httpResponse = postData(UrlList.SET_GREENLIGHT_RESULT + contentID, parameter);
        JSONObject object = (JSONObject) jsonParser.parse(
                new InputStreamReader(httpResponse.getEntity().getContent()));
        if (!checkResultData(object)) {
            return null;
        }
        HashMap<String, String> greenLightData = new HashMap<>();
        greenLightData.put("positivesize", object.get("positivesize").toString());
        greenLightData.put("negativesize", object.get("negativesize").toString());
        greenLightData.put("isChecked", object.get("isChecked").toString());
        return greenLightData;
    }

    public ArrayList<CommentListItems> getComment(String contentID) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        HttpResponse httpResponse = postData(UrlList.GET_COMMENT + contentID, null);
        JSONObject object = (JSONObject) jsonParser.parse(
                new InputStreamReader(httpResponse.getEntity().getContent()));
        if (!checkResultData(object)) {
            return null;
        }
        ArrayList<CommentListItems> list = new ArrayList<>();
        JSONArray jsonArray = (JSONArray) object.get("data");
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            String studentnumber = jsonObject.get("studentnumber").toString();
            String writername = jsonObject.get("name").toString();
            String time = jsonObject.get("time").toString();
            String comment = jsonObject.get("content").toString();
            list.add(new CommentListItems(writername, comment, studentnumber, time));
        }
        return list;
    }

    public boolean writeComment(String contentID, String comment) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("studentnumber", UserData.getInstance().getStudentNumber());
        parameter.put("comment", comment);
        HttpResponse httpResponse = postData(UrlList.WRITE_COMMENT + contentID, parameter);
        JSONObject object = (JSONObject) jsonParser.parse(
                new InputStreamReader(httpResponse.getEntity().getContent())
        );
        return checkResultData(object);
    }

    public boolean deleteBoardList(String contentID) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("studentnumber", UserData.getInstance().getStudentNumber());
        HttpResponse httpResponse = postData(UrlList.DELETE_BOARD_LIST + contentID, parameter);
        JSONObject object = (JSONObject) jsonParser.parse(
                new InputStreamReader(httpResponse.getEntity().getContent())
        );
        return checkResultData(object);
    }

    private String URLDecode(String str) throws UnsupportedEncodingException {
        return URLDecoder.decode(str, "UTF-8");
    }

    private boolean checkResultData(JSONObject jsonObject) {
        return jsonObject.get("result").equals("success");
    }

    private HttpResponse postData(String URL, HashMap<String, String> parameter) throws IOException {
        HttpPost httpPost = new HttpPost(URL);
        ContentType contentType = ContentType.create("text/plain", Charset.forName("UTF-8"));
        if (parameter != null) {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            Object[] key = parameter.keySet().toArray();
            for (Object aKey : key) {
                entityBuilder.addTextBody(aKey.toString(), parameter.get(aKey), contentType);
                log("Entity Key : " + aKey.toString());
                log("Entity Value : " + parameter.get(aKey));
            }
            httpPost.setEntity(entityBuilder.build());
        }

        return httpClient.execute(httpPost);
    }

    private HashMap<String, String> getTextParameters(String[] keystrings, String[] valuestrings) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        if (keystrings.length == valuestrings.length) {
            int size = keystrings.length;
            for (int i = 0; i < size; i++) {
                hashMap.put(keystrings[i], valuestrings[i]);
            }
        } else {
            log("getTextParameters key-value size error");
        }
        return hashMap;
    }

    private void log(String message) {
        String tag = getClass().getSimpleName();
        Log.d(tag, message);
    }

    private static int parseInt(String str) {
        return Integer.parseInt(str.replace(" ", ""));
    }
}
