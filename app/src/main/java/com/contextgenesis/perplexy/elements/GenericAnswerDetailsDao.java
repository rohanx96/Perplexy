package com.contextgenesis.perplexy.elements;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GenericAnswerDetailsDao {

    @Query("SELECT * FROM GenericAnswerDetails WHERE category = :category")
    List<GenericAnswerDetails> getByCategory(int category);

    @Query("SELECT * FROM GenericAnswerDetails WHERE question_number = :questionNumber AND category = :category LIMIT 1")
    GenericAnswerDetails getByQuestionAndCategory(int questionNumber, int category);

    @Query("SELECT * FROM GenericAnswerDetails WHERE category = :category AND status = :status LIMIT 1")
    GenericAnswerDetails getFirstByStatusAndCategory(int category, int status);

    @Query("SELECT * FROM GenericAnswerDetails WHERE category = :category AND (status = :status1 OR status = :status2)")
    List<GenericAnswerDetails> getByStatusOrStatus(int category, int status1, int status2);

    @Query("SELECT * FROM GenericAnswerDetails")
    List<GenericAnswerDetails> getAll();

    @Insert
    void insert(GenericAnswerDetails details);

    @Insert
    void insertAll(List<GenericAnswerDetails> detailsList);

    @Update
    void update(GenericAnswerDetails details);

    @Query("DELETE FROM GenericAnswerDetails")
    void deleteAll();
}
