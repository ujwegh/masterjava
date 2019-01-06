package ru.javaops.masterjava.upload;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import ru.javaops.masterjava.model.User;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

public class UserCasterByStax {

  public List<User> proceesUsersByStax(InputStream stream) throws XMLStreamException {
    List<User> userList = new ArrayList<>();

    StaxStreamProcessor processor = new StaxStreamProcessor(stream);
      while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
        String name = processor.getReader().getElementText();
        String email = processor.getAttribute("email");
        String flag = processor.getAttribute("flag");
        User user = new User(name, email, flag);
        userList.add(user);
      }
    return userList;
  }

}
