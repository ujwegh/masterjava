package ru.javaops.masterjava.xml;


import com.google.common.io.Resources;
import j2html.tags.ContainerTag;
import ru.javaops.masterjava.xml.schema.*;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;
import ru.javaops.masterjava.xml.util.XsltProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerException;
import java.io.IOException;

import java.io.InputStream;
import java.util.*;

import static j2html.TagCreator.*;

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


//        doJAXB(projectName);

//        doSTAX(projectName);

        doXSLT(projectName);

    }

    private static void doXSLT(String projectName) {

        try(InputStream xslInputStream = Resources.getResource("users.xsl").openStream();
            InputStream xmlInputStream = Resources.getResource("payload.xml").openStream()) {

            XsltProcessor processor = new XsltProcessor(xslInputStream);
            String names = processor.transform(xmlInputStream);
            System.out.println(names);
            List<String> listNames = Arrays.asList(names.split("\\n"));



        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }


    }

    private static void doSTAX(String projectName) {
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {
            XMLStreamReader reader = processor.getReader();
            Map<String, String> users = new HashMap<>();
            while (reader.hasNext()) {
                int event = reader.next();
                String email = null;
                String name = null;
                if (event == XMLEvent.START_ELEMENT) {
                    if ("User".equals(reader.getLocalName())) {
                        if (reader.getAttributeValue(3) != null && reader.getAttributeValue(3).contains(projectName)) {
                            email = reader.getAttributeValue(1);
                        }
                    }
                }

                if (event == XMLEvent.START_ELEMENT) {
                    if ("fullName".equals(reader.getLocalName())) {
                        name = reader.getLocalName();
                    }
                }
                users.put(email, name);
            }

            System.out.println(usersToHtml(users, projectName));


        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }


    }

//    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
//        Map<K, V> result = new LinkedHashMap<>();
//        Stream<Map.Entry<K, V>> st = map.entrySet().stream();
//        st.sorted(Comparator.comparing(e -> e.getValue())).forEach(e -> result.put(e.getKey(), e.getValue()));
//        return result;
//    }

    private static String usersToHtml(Map<String, String> list, String projectName) {
        ContainerTag htmlTable = table().attr("border", "1")
                .attr("cellpadding", "10")
                .attr("cellspacing", "0");

        htmlTable.with(h2(projectName));
        htmlTable.with(tr().with(th("User name"), th("User email")));
        for (Map.Entry<String, String> entry : list.entrySet()) {
            htmlTable.with(tr().with(td(entry.getKey()), td(entry.getValue())));
        }

        return html().with(title("Html table")).with(body().with(htmlTable)).render();
    }

    private static void doJAXB(String projectName) {
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
