package ru.javaops.masterjava.xml;


import com.google.common.io.Resources;
import j2html.tags.ContainerTag;
import ru.javaops.masterjava.xml.schema.*;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import javax.xml.bind.JAXBException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static j2html.TagCreator.*;
import static javax.management.Query.attr;

// Реализовать класс MainXml, который принимает параметром имя
// проекта в тестовом xml и выводит отсортированный список его участников (использовать JAXB).
// Из списка участников сделать html таблицу (имя/email). Реализация- любая.
public class MainXml {

    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);

    static {
        JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
    }

    public static void main(String[] args) {
        String projectName = args[0];

        try {
            Payload payload = JAXB_PARSER.unmarshal(Resources.getResource("payload.xml").openStream());
            List<Project> projects = payload.getProjects().getProject();
            List<User> users = payload.getUsers().getUser();
            List<String> groupNames = new ArrayList<>();

            for (Project proj : projects) {
                for (Project.Group group : proj.getGroup()) {
                    groupNames.add(group.getName());
                }
            }

            List<User> sortedUsers = getSortedUsers(users, groupNames);

            for (User user : sortedUsers) {
                System.out.println(user.getFullName());
            }

            String html = usersToHTML(sortedUsers, projectName);
            System.out.println(html);


        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }

    }

    private static String usersToHTML(List<User> sortedUsers, String projectName) {

        ContainerTag htmlTable = table().attr("border", "1")
                .attr("cellpadding", "10")
                .attr("cellspacing", "0");

        htmlTable.with(h2(projectName));
        htmlTable.with(tr().with(th("User name"), th("User email")));
        for (User user : sortedUsers) {
            htmlTable.with(tr().with(td(user.getFullName()), td(user.getEmail())));
        }

        return html().with(title("Html table")).with(body().with(htmlTable)).render();
    }

    private static List<User> getSortedUsers(List<User> users, List<String> groupNames) {
        List<User> participantUsers = new ArrayList<>();
        for (User user : users) {
            for (Object group : user.getGroupRef()) {
                if (checkContain(group, groupNames)) {
                    participantUsers.add(user);
                }
            }
        }
        if (!participantUsers.isEmpty()) {
            participantUsers.sort(Comparator.comparing(User::getFullName));
        }

        return participantUsers;
    }

    private static boolean checkContain(Object group, List<String> groupNames) {
        for (String name : groupNames) {
            Project.Group group1 = (Project.Group) group;
            return group1.getName().contains(name);
        }
        return false;
    }
}
