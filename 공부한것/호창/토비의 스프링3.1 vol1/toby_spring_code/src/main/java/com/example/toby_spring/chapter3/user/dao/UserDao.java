package com.example.toby_spring.chapter3.user.dao;

import com.example.toby_spring.chapter3.user.dao.strategy.AddStatement;
import com.example.toby_spring.chapter3.user.dao.strategy.DeleteAllStatement;
import com.example.toby_spring.chapter3.user.dao.strategy.StatementStrategy;
import com.example.toby_spring.chapter3.user.domain.User;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@NoArgsConstructor
public class UserDao {

    private JdbcTemplate jdbcTemplate;
    private RowMapper<User> userMapper =
            new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    return user;
                }
            };
    public void add(final User user) {
        jdbcTemplate.update("insert into users values(?,?,?)", user.getId(), user.getName(), user.getPassword());
    }

    public User get(String id) {
        return jdbcTemplate.queryForObject("select * from users where id = ?", new Object[]{id}, userMapper);
    }

    public void deleteAll() {
        jdbcTemplate.update("delete from users");
    }

    public int getCount() throws SQLException {
        return jdbcTemplate.query("select count(*) from users", new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                rs.next();
                return rs.getInt(1);
            }
        });

    }

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<User> getAll() {
        return jdbcTemplate.query("select * from users order by id", userMapper);

    }
}
