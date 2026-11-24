package com.gamehub.infrastructure;
import java.sql.*;
public final class QueryExecutor { private QueryExecutor(){} public static ResultSet query(PreparedStatement statement)throws SQLException{return statement.executeQuery();} public static int update(PreparedStatement statement)throws SQLException{return statement.executeUpdate();} public static ResultSet keys(PreparedStatement statement)throws SQLException{return statement.getGeneratedKeys();} }
