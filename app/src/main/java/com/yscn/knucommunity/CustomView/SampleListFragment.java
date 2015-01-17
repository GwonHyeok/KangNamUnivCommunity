package com.yscn.knucommunity.CustomView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yscn.knucommunity.R;

import java.util.ArrayList;

public class SampleListFragment extends ScrollTabHolderFragment implements OnScrollListener {

    private static final String ARG_POSITION = "position";
    private static Type type;
    private ListView mListView;
    private ArrayList<String> mListItems;
    private int mPosition;

    public synchronized static Fragment newInstance(int position, Type type) {
        SampleListFragment.type = type;
        SampleListFragment f = new SampleListFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt(ARG_POSITION);

        mListItems = new ArrayList<String>();
        if (type == Type.NOTICE) {
            mListItems.add("독일바이마르음악학부 교수연주회");
            mListItems.add("[특강] 뇌학습을 통한 자신의 학습스타일 찾기");
            mListItems.add("독일 함부르크응용과학대학 교환학생 선발결과 공지");
            mListItems.add("캄보디아 희망도서관 건립을 위한 오페라 Gala Concert와 찬양의 밤");
            mListItems.add("2014-2학기 장애대학생 도우미 추가 모집");
            mListItems.add("제 8회 독일바이마르음악학부 전국학생콩쿨 수상 결과 안내");
            mListItems.add("2014학년도 2학기 영어집중 회화 특강");
            mListItems.add("음악교육신문 초청 듀오 콘서트 Fall in Piano 오지현교수 연주회");
            mListItems.add("[11.04~11.06] 채플(추수감사예배) 안내");
            mListItems.add("2014-2학기 11월 교직원예배 안내");
        } else if (type == Type.HAKSA) {
            mListItems.add("외래강사 공개초빙 안내");
            mListItems.add("2014학년도 전기 졸업대상자 졸업유보 신청 안내");
            mListItems.add("조기졸업 신청 안내");
            mListItems.add("전공이수 신청 및 전공이수 변경 신청 안내");
            mListItems.add("2014학년도 동계 계절수업 개설 희망과목 신청 안내");
            mListItems.add("2014학년도 명강의 에세이 공모전 안내");
            mListItems.add("2015학년도 신입생 수시모집 전형 진행에 따른 샬롬관 출입 제한 및 해당 수업 강의실 이동 안내");
            mListItems.add("2014 - 2학기 중간고사 시험진행 협조 신청 방법 안내");
            mListItems.add("2015년 2월 졸업자 졸업종합평가 학부(과)별 세부 계획 안내");
            mListItems.add("2014 - 2학기 학생 “수업피드백시스템(TFS)” 참여 안내");
            mListItems.add("2014학년도 2학기 인정학점 승인 확정 안내");
        } else if (type == Type.JANG) {
            mListItems.add("[필독] 장학금 및 학자금대출 이중(중복)수혜 기준 및 유의사항 안내");
            mListItems.add("[유의사항] 보훈자녀 장학생 유의사항(등록,휴학,수강신청 등) 안내");
            mListItems.add("[필독] 2015년 국가장학금 및 학자금대출 가구원 정보제공 사전동의 신청 안내");
            mListItems.add("[장학] 2014-2학기 심전생활관 장학금 지급 안내");
            mListItems.add("[장학] 2014-2학기 공로장학생 선발확정 및 장학금 지급 안내(2차)");
            mListItems.add("제14기 심전국제교류재단 장학생 선발안내");
            mListItems.add("[국가근로] 2014학년도 국가근로장학사업 동계방학 중 외부기관 신청 안내");
            mListItems.add("2014-2학기 강남사랑2 장학금 2차 지급 안내");
            mListItems.add("[학자금] 2014-2학기 전라남도 대학생 학자금 대출이자 지원사업 신청 안내");
            mListItems.add("[학자금] 학자금대출 연체에 따른 신용유의정보 등록 유의사항 안내");
            mListItems.add("[국가근로] 2014학년도 국가근로장학사업 동계방학 중 교내근로 신청 안");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.custom_fragment_list, null);

        mListView = (ListView) v.findViewById(R.id.listView);

        View placeHolderView = inflater.inflate(R.layout.custom_view_header_placeholder, mListView, false);
        mListView.addHeaderView(placeHolderView);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView.setOnScrollListener(this);
        mListView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.custom_list_item, android.R.id.text1, mListItems));
    }

    @Override
    public void adjustScroll(int scrollHeight) {
        if (scrollHeight == 0 && mListView.getFirstVisiblePosition() >= 1) {
            return;
        }

        mListView.setSelectionFromTop(1, scrollHeight);

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mScrollTabHolder != null)
            mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, mPosition);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // nothing
    }

    public static enum Type {NOTICE, HAKSA, JANG}

}