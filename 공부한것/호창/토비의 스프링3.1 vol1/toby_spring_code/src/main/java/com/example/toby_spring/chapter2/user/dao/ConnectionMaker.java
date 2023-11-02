package com.example.toby_spring.chapter2.user.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionMaker {
    Connection makeConnection() throws ClassNotFoundException, SQLException;
}
