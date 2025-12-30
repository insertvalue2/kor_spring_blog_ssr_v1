package org.example.demo_ssr_v1_1.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {


    @Query(value = "SELECT DISTINCT b FROM Board b JOIN FETCH b.user ORDER BY b.createdAt DESC",
    countQuery = "SELECT COUNT(DISTINCT b) FROM Board b")
    Page<Board> findAllWithUserOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 게시글 검색(제목 또는 내용, 페이징 포함)
     */
    @Query(value = "SELECT DISTINCT b FROM Board b JOIN FETCH b.user " +
            "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%')) ",
            countQuery = "SELECT COUNT(DISTINCT b) FROM Board b " +
                    "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "   OR LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Board> findByTitleContainingOrContentContaining(@Param("keyword") String keyword, Pageable pageable);


    // 게시글 ID로 조회 (작성자 정보 포함 - JOIN FETCH 사용해야 함)
    @Query("SELECT b FROM Board b JOIN FETCH b.user WHERE b.id = :id")
    Optional<Board> findByIdWithUser(@Param("id") Long id);

}
