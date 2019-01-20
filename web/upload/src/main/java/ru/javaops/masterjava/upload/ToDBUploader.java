package ru.javaops.masterjava.upload;

import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;

import java.util.List;

public class ToDBUploader {

    public void upload(List<User> users, int chunkSize) {
        UserDao dao = DBIProvider.getDao(UserDao.class);

        DBIProvider.getDBI().useTransaction((conn, status) -> {
            dao.insertAll(users, chunkSize);
//            users.forEach(dao::insert);
        });
    }
}
