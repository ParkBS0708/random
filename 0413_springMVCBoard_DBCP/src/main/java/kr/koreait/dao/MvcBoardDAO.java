package kr.koreait.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import kr.koreait.vo.MvcBoardVO;

public class MvcBoardDAO {

	private DataSource dataSource;
	
//	기본 생성자에서 데이터베이스와 연결한다.
	public MvcBoardDAO() {
		try {
			Context context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:/comp/env/jdbc/oracle");
			System.out.println("아싸~~~~ 연결성공!!!");
		} catch (NamingException e) {
			e.printStackTrace();
			System.out.println("연결실패!!!");
		}
	}
	
//	InsertService 클래스에서 호출되는 테이블에 저장할 메인글 데이터가 저장된 객체를 넘겨받고 insert sql 명령을 실행하는 메소드
	public void insert(MvcBoardVO mvcBoardVO) {
		System.out.println("MvcBoardDAO 클래스의 insert() 메소드 실행");
//		System.out.println(mvcBoardVO);
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = dataSource.getConnection();
			String sql = "insert into mvcboard (idx, name, subject, content, ref, lev, seq) " +
					"values (mvcboard_idx_seq.nextval, ?, ?, ?, mvcboard_idx_seq.currval, 0, 0)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, mvcBoardVO.getName());
			pstmt.setString(2, mvcBoardVO.getSubject());
			pstmt.setString(3, mvcBoardVO.getContent());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) { try { conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

//	SelectService 클래스에서 호출되는 테이브에 저장된 전체 글의 개수를 얻어오는 select sql 명령을 실행하는 메소드
	public int selectCount() {
		System.out.println("MvcBoardDAO 클래스의 selectCount() 메소드 실행");
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = 0;			// 테이블에 저장된 전체 글의 개수를 기억할 변수를 선언한다.
		try {
			conn = dataSource.getConnection();
			String sql = "select count(*) from mvcboard";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			rs.next();			// 개수는 null이 나올리 없으로 조건을 비교할 필요없다.
			result = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) { try { conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
		return result;		// 전체 글의 개수를 리턴한다.
	}

//	SelectService 클래스에서 호출되는 브라우저 화면에 표시할 1페이지 분량의 시작 인덱스, 끝 인덱스가 저장된 HashMap 객체를 넘겨받고 테이브에서
//	1페이지 분량의 글을 얻어오는 select sql 명령을 실행하는 메소드
	public ArrayList<MvcBoardVO> selectList(HashMap<String, Integer> hmap) {
		System.out.println("MvcBoardDAO 클래스의 selectList() 메소드 실행");
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<MvcBoardVO> list = null;		// 1페이지 분량의 글을 기억할 ArrayList
		try {
			conn = dataSource.getConnection();
			
//			1페이지 분량의 글을 얻어와서 ResultSet 객체에 저장시킨다.
			String sql = "select * from ("
					   + 	"select rownum rnum, AA.* from ("
					   + 		"select * from mvcboard order by ref desc, seq asc"
					   + 	") AA where rownum <= ?"
					   + ") where rnum >= ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, hmap.get("endNo"));
			pstmt.setInt(2, hmap.get("startNo"));
			rs = pstmt.executeQuery();
			
//			ResultSet 객체에 저장된 1페이지 분량의 글을 리턴시키기 위해 ArrayList 객체를 생성한다.
			list = new ArrayList<MvcBoardVO>();
//			ResultSet 객체에 저장된 글이 없을 때 까지 반복하며 ArrayList에 저장한다.
			while (rs.next()) {
				AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:applicationCTX.xml");
				MvcBoardVO mvcBoardVO = ctx.getBean("mvcBoardVO", MvcBoardVO.class);
				mvcBoardVO.setIdx(rs.getInt("idx"));
				mvcBoardVO.setName(rs.getString("name"));
				mvcBoardVO.setSubject(rs.getString("subject"));
				mvcBoardVO.setContent(rs.getString("content"));
				mvcBoardVO.setRef(rs.getInt("ref"));
				mvcBoardVO.setLev(rs.getInt("lev"));
				mvcBoardVO.setSeq(rs.getInt("seq"));
				mvcBoardVO.setHit(rs.getInt("hit"));
				mvcBoardVO.setWriteDate(rs.getTimestamp("writeDate"));
				list.add(mvcBoardVO);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) { try { conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
		return list;
	}

//	IncrementService 클래스에서 조회수를 증가시킬 글번호를 넘겨받고 조회수를 증가시키는 update sql 명령을 실행하는 메소드
	public void increment(int idx) {
		System.out.println("MvcBoardDAO 클래스의 increment() 메소드 실행");
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = dataSource.getConnection();
			
//			제목을 클릭한 글의 조회수르 증가시킨다.
			String sql = "update mvcboard set hit = hit + 1 where idx = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, idx);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) { try { conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

//	ContentViewService 클래스에서 조회수를 증가시킨 글번호를 넘겨받고 조회수를 증가시킨 글 1건을 얻어오는 select sql 명령을 실행하는 메소드
	public MvcBoardVO selectByIdx(int idx) {
		System.out.println("MvcBoardDAO 클래스의 increment() 메소드 실행");
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		MvcBoardVO mvcBoardVO = null;
		try {
			conn = dataSource.getConnection();
			
//			글 1건을 얻어오는 sql 명령을 실행해서 브라우저에 출력할 글 1건을 얻어서 ResultSet 객체에 저장한다.
			String sql = "select * from mvcboard where idx = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, idx);
			rs = pstmt.executeQuery();
			
//			ResultSet 객체에 저장된 브라우저에 출력할 글 1건을 MvcBoardVO 클래스 객체에 저장한다.
			if(rs.next()) {
				AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:applicationCTX.xml");
				mvcBoardVO = ctx.getBean("mvcBoardVO", MvcBoardVO.class);
				mvcBoardVO.setIdx(rs.getInt("idx"));
				mvcBoardVO.setName(rs.getString("name"));
				mvcBoardVO.setSubject(rs.getString("subject"));
				mvcBoardVO.setContent(rs.getString("content"));
				mvcBoardVO.setRef(rs.getInt("ref"));
				mvcBoardVO.setLev(rs.getInt("lev"));
				mvcBoardVO.setSeq(rs.getInt("seq"));
				mvcBoardVO.setHit(rs.getInt("hit"));
				mvcBoardVO.setWriteDate(rs.getTimestamp("writeDate"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) { try { conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
		return mvcBoardVO;
	}

//	DeleteService 클래스에서 삭제할 글번호를 넘겨받고 글 1건을 삭제하는 delete sql 명령을 실행하는 메소드
	public void delete(int idx) {
		System.out.println("MvcBoardDAO 클래스의 delete() 메소드 실행");
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = dataSource.getConnection();
			String sql = "delete from mvcboard where idx = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, idx);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) { try { conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

//	UpdateService 클래스에서 수정할 글번호와 데이터를 넘겨받고 글 1건을 수정하는 update sql 명령을 실행하는 메소드
	public void update(int idx, String subject, String content) {
		System.out.println("MvcBoardDAO 클래스의 update() 메소드 실행");
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = dataSource.getConnection();
			String sql = "update mvcboard set subject = ?, content = ? where idx = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, subject);
			pstmt.setString(2, content);
			pstmt.setInt(3, idx);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) { try { conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

//	ReplyService 클래스에서 호출되는 글그룹과 글이 출력되는 순서가 저장된 HashMap 객체를 넘겨받고 조건에 만족하는 seq를 1씩 증가시키는
//	update sql 명령을 실행하는 메소드
	public void replyIncrement(HashMap<String, Integer> hmap) {
		System.out.println("MvcBoardDAO 클래스의 replyIncrement() 메소드 실행");
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = dataSource.getConnection();
			String sql = "update mvcboard set seq = seq + 1 where ref = ? and seq >= ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, hmap.get("ref"));
			pstmt.setInt(2, hmap.get("seq"));
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) { try { conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}	
	}

//	ReplyService 클래스에서 호출되는 답글이 저장된 객체를 넘겨받고 답글을 저장하는 insert sql 명령을 실행하는 메소드
	public void replyInsert(MvcBoardVO mvcBoardVO) {
		System.out.println("MvcBoardDAO 클래스의 replyInsert() 메소드 실행");
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = dataSource.getConnection();
			String sql = "insert into mvcboard (idx, name, subject, content, ref, lev, seq) values (mvcboard_idx_seq.nextval, ?, ?, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, mvcBoardVO.getName());
			pstmt.setString(2, mvcBoardVO.getSubject());
			pstmt.setString(3, mvcBoardVO.getContent());
			pstmt.setInt(4, mvcBoardVO.getRef());
			pstmt.setInt(5, mvcBoardVO.getLev());
			pstmt.setInt(6, mvcBoardVO.getSeq());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) { try { conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}
		   
}
















