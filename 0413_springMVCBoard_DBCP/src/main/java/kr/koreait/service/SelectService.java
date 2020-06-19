package kr.koreait.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.ui.Model;

import kr.koreait.dao.MvcBoardDAO;
import kr.koreait.vo.MvcBoardList;

public class SelectService implements MvcBoardService {

	@Override
	public void execute(Model model) {
		System.out.println("SelectService 클래스의 execute() 메소드 실행");
		Map<String, Object> map = model.asMap();
		HttpServletRequest request = (HttpServletRequest) map.get("request");
		
//		MvcBoardDAO 클래스의 bean을 얻어온다.
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:applicationCTX.xml");
		MvcBoardDAO mvcBoardDAO = ctx.getBean("mvcBoardDAO", MvcBoardDAO.class);
		
//		브라우저 화면에 출력할 글의 개수를 정한다.
		int pageSize = 10;
//		컨트롤러에서 HttpServletRequest 인터페이스 객체에 저장되서 넘어온 화면에 표시할 페이지 번호를 받는다.
		int currentPage = 1;
		try {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		} catch (NumberFormatException e) { }
//		테이블에 저장된 전체 글의 개수를 얻어온다.
		int totalCount = mvcBoardDAO.selectCount();
//		System.out.println(totalCount);
		
//		1페이지 분량의 글과 페이지 작업에 사용할 8개의 변수를 기억하는 MvcBoardList 클래스의 bean을 얻어온다.
		MvcBoardList mvcBoardList = ctx.getBean("mvcBoardList", MvcBoardList.class); // 기본 생성자로 생성된 bean
//		페이지 작업에 사용할 8개의 변수를 초기화시키는 메소드를 실행한다.
		mvcBoardList.initMvcBoardList(pageSize, totalCount, currentPage);
		
//		MvcBoardList 크래스의 1페이지 분량의 글을 기억하는 ArrayList에 1페이지 분량의 글을 테이블에서 얻어와서 넣어준다.
		HashMap<String, Integer> hmap = new HashMap<String, Integer>();
		hmap.put("startNo", mvcBoardList.getStartNo());
		hmap.put("endNo", mvcBoardList.getEndNo());
		mvcBoardList.setMvcBoardList(mvcBoardDAO.selectList(hmap));
//		System.out.println(mvcBoardList);
		
//		list.jsp로 넘겨줄 데이터를 Model 인터페이스 객체에 넣어준다.
		model.addAttribute("mvcBoardList", mvcBoardList);
	}

}

















