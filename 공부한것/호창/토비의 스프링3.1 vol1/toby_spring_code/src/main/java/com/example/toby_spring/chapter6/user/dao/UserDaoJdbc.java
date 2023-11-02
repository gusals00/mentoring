package com.example.toby_spring.chapter6.user.dao;

import com.example.toby_spring.chapter6.user.domain.Level;
import com.example.toby_spring.chapter6.user.domain.User;
import lombok.NoArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@NoArgsConstructor
public class UserDaoJdbc implements UserDao {

    private JdbcTemplate jdbcTemplate;
    private RowMapper<User> userMapper =
            new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    user.setLevel(Level.valueOf(rs.getInt("level")));
                    user.setLogin(rs.getInt("login"));
                    user.setRecommend(rs.getInt("recommend"));
                    user.setEMail(rs.getString("email"));
                    return user;
                }
            };

    @Override
    public void add(final User user) {
        jdbcTemplate.update("insert into users values(?,?,?,?,?,?,?)", user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(),user.getEMail());
    }

    @Override
    public User get(String id) {
        return jdbcTemplate.queryForObject("select * from users where id = ?", new Object[]{id}, userMapper);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from users");
    }

    @Override
    public int getCount() {
        return jdbcTemplate.query("select count(*) from users", new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                rs.next();
                return rs.getInt(1);
            }
        });

    }

    @Override
    public void update(User user) {
        this.jdbcTemplate.update("update users set name= ?, password = ?, level = ?,login = ?,recommend = ? where id= ? ",
                user.getName(),user.getPassword(),user.getLevel().intValue(),user.getLogin(),user.getRecommend(),user.getId());
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("select * from users order by id", userMapper);
    }

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

}
