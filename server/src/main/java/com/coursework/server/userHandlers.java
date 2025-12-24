package com.coursework.server;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import com.coursework.server.database.PublicUser;
import com.coursework.server.database.User;
import com.coursework.server.database.UsersDB;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

class GetUserHandler implements Handler {
    @Override
    public void handle(Context ctx) {
        Map<String, List<String>> params = ctx.queryParamMap();
        Long id = null;
        String login = null;
        List<String> idList = params.get("id");
        List<String> logins = params.get("login");
        if (idList != null && !idList.isEmpty() && idList.get(0) != null)
            id = Long.valueOf(idList.get(0));
        if (logins != null && !logins.isEmpty() && logins.get(0) != null)
            login = logins.get(0);
        // if id != null login ignored
        
        if (id != null && id <= 0) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.result("incorrect id");
            return;
        }
        if (id == null && login == null) {
            ctx.status(HttpStatus.BAD_REQUEST);
            return;
        }

        try {
            String result;
            if (id == null)
                result = getByLogin(login);
            else
                result = getById(id);
            ctx.status(HttpStatus.OK);
            ctx.result(result.getBytes(StandardCharsets.UTF_8));
        } catch (SQLException e) {
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getByLogin(String login) throws SQLException {
        UsersDB usersDB = UsersDB.getInstance();
        User user = usersDB.getByLogin(login);
        PublicUser publicUser = null;
        if (user != null) publicUser = new PublicUser(user);
        return new Gson().toJson(publicUser);
    }

    private String getById(Long id) throws SQLException {
        UsersDB usersDB = UsersDB.getInstance();
        User user = usersDB.getById(id);
        PublicUser publicUser = null;
        if (user != null) publicUser = new PublicUser(user);
        return new Gson().toJson(publicUser);
    }
}


class JWTInfo {
    // TODO: Реализовать секретное хранение
    public static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public static final long EXPIRATION_MS = 15 * 60 * 1000; // 15 минут
};


class UserAuthentication implements Handler {
    @Override
    public void handle(Context ctx) {
        String body = ctx.body();

        try {
            Gson gson = new Gson();
            Map<String, String> info = gson.fromJson(body, new TypeToken<Map<String, String>>(){}.getType());
            String login = info.get("login");
            String password = info.get("password");
            if (login == null || password == null) {
                ctx.status(HttpStatus.BAD_REQUEST);
                return;
            }
            User user = UsersDB.getInstance().getByLogin(login);

            if (user == null || !user.checkPassword(password)) {
                ctx.status(HttpStatus.UNAUTHORIZED);
                return;
            }
            String jwt = Jwts.builder()
                    .setSubject(user.getLogin())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + JWTInfo.EXPIRATION_MS))
                    .signWith(JWTInfo.SECRET_KEY)
                    .compact();

            ctx.status(HttpStatus.OK);
            ctx.result(jwt);
        } catch (SQLException e) {
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

class UserRegistration implements Handler {
    @Override
    public void handle(Context ctx) {
        String body = ctx.body();

        try {
            UsersDB userDB = UsersDB.getInstance();
            Gson gson = new Gson();
            Map<String, String> info = gson.fromJson(body, new TypeToken<Map<String, String>>(){}.getType());
            String login = info.get("login");
            String password = info.get("password");
            String name = info.get("name");
            String surname = info.get("surname");
            if (login == null || password == null || name == null || surname == null) {
                ctx.status(HttpStatus.BAD_REQUEST);
                return;
            }
            User user = userDB.getByLogin(login);
            if (user != null) {
                ctx.status(HttpStatus.CONFLICT);
                ctx.result("login already exists");
                return;
            }
            user = User.fromPassword(login, password, name, surname);
            userDB.insertUser(user);

            String jwt = Jwts.builder()
                    .setSubject(user.getLogin())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + JWTInfo.EXPIRATION_MS))
                    .signWith(JWTInfo.SECRET_KEY)
                    .compact();

            ctx.status(HttpStatus.OK);
            ctx.result(jwt);
        } catch (SQLException e) {
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}