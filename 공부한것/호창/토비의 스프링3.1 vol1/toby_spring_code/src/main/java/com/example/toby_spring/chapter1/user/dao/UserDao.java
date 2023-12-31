package com.example.toby_spring.chapter1.user.dao;

import com.example.toby_spring.chapter1.user.domain.User;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.sql.DataSource;
import java.lang.ref.PhantomReference;
import java.sql.*;

@NoArgsConstructor
public class UserDao {

    @Setter
    private DataSource dataSource;

    public void add(User user) throws ClassNotFoundException, SQLException {
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement(
                "insert into users(id, name, password) values(?,?,?)");
        ps.setString(1,user.getId());
        ps.setString(2,user.getName());
        ps.setString(3,user.getPassword());

        ps.executeUpdate();

        ps.close();
        c.close();
    }

    public User get(String id)throws ClassNotFoundException, SQLException{
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement(
                "select * from users where id = ?");
        ps.setString(1,id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));

        rs.close();
        ps.close();
        c.close();

        return user;
    }

}
