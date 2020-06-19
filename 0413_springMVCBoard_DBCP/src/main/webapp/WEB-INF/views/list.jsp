<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>

<style type="text/css">

	a { color: black; text-decoration: none; }
	a:hover { color: white; font-weight: bold; background: red; }

</style>

</head>
<body>

<table width="1000" align="center" border="1" cellpadding="5" cellspacing="0">
	<tr><th colspan="5">게시판 보기</th></tr>
	<tr>
		<td colspan="5" align="right">
			${mvcBoardList.totalCount}(${mvcBoardList.currentPage}/${mvcBoardList.totalPage})
		</td>
	</tr>
	<tr>
		<td width="80" align="center">글번호</td>
		<td width="100" align="center">이름</td>
		<td width="620" align="center">제목</td>
		<td width="120" align="center">작성일</td>
		<td width="80" align="center">조회수</td>
	</tr>
	
	<!-- request 영역의 mvcBoardList 객체에서 1페이지 분량의 글이 저장된 ArrayList(mvcBoardList)의 내용만 얻어내서 별도의 변수에 저장한다. -->
	<c:set var="list" value="${mvcBoardList.mvcBoardList}"/>
	
	<!-- 테이블에 글이 없으면 없다고 출력한다. -->
	<c:if test="${list.size() == 0}">
	<tr>
		<td colspan="5" align="center">테이블에 저장된 글이 없습니다.</td>
	</tr>
	</c:if>
	
	<!-- 테이블에 글이 있으면 화면에 출력할 글의 개수 만큼 반복하며 글을 출력한다. -->
	<c:if test="${list.size() != 0}">
	
	<!-- 컴퓨터 시스템의 현재 날짜와 시간을 기억하는 Date 클래스 객체를 만든다. -->
	<jsp:useBean id="date" class="java.util.Date"/>
	
	<c:forEach var="vo" items="${list}">
	<tr>
		<td align="center">${vo.idx}</td>
		<td align="center">
			<c:set var="name" value="${fn:replace(fn:trim(vo.name), '<', '&lt;')}"/>
			<c:set var="name" value="${fn:replace(name, '>', '&gt;')}"/>
			${name}
		</td>
		<td>
		
			<!-- 레벨에 따른 들여쓰기 -->
			<c:if test="${vo.lev > 0}">
				<c:forEach var="i" begin="1" end="${vo.lev}" step="1">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</c:forEach>
				<img src="images/reply.png"/>
			</c:if> 
			
			<!-- 오늘 입력된 글은 new를 표시한다. -->
			<c:if test="${date.year == vo.writeDate.year && date.month == vo.writeDate.month && date.date == vo.writeDate.date}">
				<img src="images/new.png"/>
			</c:if>
			
			<c:set var="subject" value="${fn:replace(fn:trim(vo.subject), '<', '&lt;')}"/>
			<c:set var="subject" value="${fn:replace(subject, '>', '&gt;')}"/>
			<!-- 제목에 하이퍼링크를 걸어준다. => 하이퍼링크를 클릭하면 조회수를 증가시키고 클릭된 글의 내용을 표시한다. -->
			<a href="increment?idx=${vo.idx}&currentPage=${mvcBoardList.currentPage}">
				${subject}
			</a>
			
			<!-- 조회수가 10번을 넘으면 hot을 표시한다. -->
			<c:if test="${vo.hit > 10}">
				<img src="images/hot.gif"/>
			</c:if>
			
		</td>
		<td align="center">
			<!-- 오늘 입력된 글은 시간만 어제 이전에 입력된 글은 날짜만 표시한다. -->
			<c:if test="${date.year == vo.writeDate.year && date.month == vo.writeDate.month && date.date == vo.writeDate.date}">
				<fmt:formatDate value="${vo.writeDate}" pattern="a h:mm"/>
			</c:if>
			<c:if test="${date.year != vo.writeDate.year || date.month != vo.writeDate.month || date.date != vo.writeDate.date}">
				<fmt:formatDate value="${vo.writeDate}" pattern="yyyy.MM.dd(E)"/>
			</c:if>
		</td>
		<td align="center">${vo.hit}</td>
	</tr>
	</c:forEach>
	</c:if>
	
	<!-- 페이지 이동 버튼 -->
	<tr>
		<td align="center" colspan="5">
		
		<!-- 처음으로, 10페이지 앞으로 -->
		<c:if test="${mvcBoardList.startPage > 1}">
			<input type="button" value="start page" onclick="location.href='?currentPage=1'" title="첫 페이지로 이동합니다."/>
			<input type="button" value="-10 page" 
					onclick="location.href='?currentPage=${mvcBoardList.startPage - 1}'" 
					title="이전 10 페이지로 이동합니다."/>
		</c:if>
		
		<c:if test="${mvcBoardList.startPage <= 1}">
			<input type="button" value="start page" disabled="disabled" title="이미 첫 페이지 입니다."/>
			<input type="button" value="-10 page" disabled="disabled" title="이전 10 페이지가 없습니다."/>
		</c:if>
		
		<!-- 페이지 이동 -->
		<c:forEach var="i" begin="${mvcBoardList.startPage}" end="${mvcBoardList.endPage}" step="1">
		
			<c:if test="${i == mvcBoardList.currentPage}">
				<input class="button button2" type="button" value="${i}" disabled="disabled"/>
			</c:if>
			
			<c:if test="${i != mvcBoardList.currentPage}">
				<input class="button button1" type="button" value="${i}" onclick="location.href='?currentPage=${i}'" 
					title="${i}페이지로 이동합니다."/>
			</c:if>
		
		</c:forEach>
		
		<!-- 마지막으로, 10페이지 뒤로 -->
		<c:if test="${mvcBoardList.endPage < mvcBoardList.totalPage}">
			<input type="button" value="+10 page" 
					onclick="location.href='?currentPage=${mvcBoardList.endPage + 1}'" title="다음 10 페이지로 이동합니다."/>
			<input type="button" value="end page" 
					onclick="location.href='?currentPage=${mvcBoardList.totalPage}'" title="마지막 페이지로 이동합니다."/>
		</c:if>

		<c:if test="${mvcBoardList.endPage >= mvcBoardList.totalPage}">
				<input type="button" value="+10 page" disabled="disabled" title="다음 10 페이지가 없습니다."/>
			<input type="button" value="end page" disabled="disabled" title="이미 마지막 페이지 입니다."/>
		</c:if>
		
		</td>
	</tr>
	
	<!-- 글쓰기 페이지로 이동하는 버튼 -->
	<tr>
		<td align="right" colspan="5">
			<input type="button" value="글쓰기" onclick="location.href='insert'"/>
		</td>
	</tr>	
	
	
</table>


</body>
</html>













