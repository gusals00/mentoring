package com.example.toby_spring.chapter3.user.dao.strategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteAllStatement implements StatementStrategy{
    @Override
    public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
        return c.prepareStatement("delete from users");
    }
}
