package model.dao;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public interface EntitiesDAO<T> {
	T save(T obj) throws SQLException;
	T update(T obj)throws SQLException;
	T find(Long id);
	List<T> findAll();
	void delete(T obj) throws SQLException;
}
