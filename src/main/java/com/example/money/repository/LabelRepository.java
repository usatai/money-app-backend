package com.example.money.repository;

import com.example.money.model.Label;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelRepository extends JpaRepository<Label,Integer> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value= """
        delete from label where user_id = :user_id AND label_id = :label_id AND TO_CHAR(create_date,'YYYY-MM') = :yearMonthSt
        """
        ,nativeQuery = true)
        void deleteLabel(@Param("user_id") Integer userIdInt, @Param("label_id") Integer label_id, @Param("yearMonthSt")String yearMonthSt);


    @Query(value = "select label_id from label where user_id = :user_id AND label_name = :label_name AND TO_CHAR(create_date,'YYYY-MM') = :yearMonthSt",nativeQuery = true)
    Integer searchLabel_id(@Param("user_id") Integer userIdInt,@Param("label_name") String label_name,@Param("yearMonthSt")String yearMonthSt);

    @Query(value= "SELECT CASE WHEN EXISTS (" +
              "  SELECT 1 FROM label " +
              "  WHERE label_name = :label_name " +
              "  AND user_id = :user_id " +
              "  AND DATE_TRUNC('month', create_date) = DATE_TRUNC('month', NOW())" +
              ") THEN 1 ELSE 0 END",
       nativeQuery = true)
    Long existsByLabel(@Param("label_name") String label_name, @Param("user_id") Integer user_id);
}
