package com.nflabs.zeppelin.notebook;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.nflabs.zeppelin.interpreter.Interpreter;
import com.nflabs.zeppelin.interpreter.InterpreterException;
import com.nflabs.zeppelin.interpreter.InterpreterFactory;
import com.nflabs.zeppelin.interpreter.InterpreterGroup;
import com.nflabs.zeppelin.interpreter.InterpreterSetting;

/**
 * Repl loader per note.
 */
public class NoteInterpreterLoader {
  private transient InterpreterFactory factory;
  List<String> interpreterSettingIds;
  
  public NoteInterpreterLoader(InterpreterFactory factory) {
    this.factory = factory;
    interpreterSettingIds = Collections.synchronizedList(new LinkedList<String>());
    
    // if no saved setting found, take default settings.
    interpreterSettingIds.addAll(factory.getDefaultInterpreterList());
  }
  
  /**
   * set interpreter ids
   * @param ids InterpreterSetting id list
   */
  public void setInterpreters(List<String> ids) {
    synchronized (interpreterSettingIds) {
      interpreterSettingIds.clear();
      interpreterSettingIds.addAll(ids);
    }
  }
  
  public List<String> getInterpreters() {
    LinkedList<String> settingIds = new LinkedList<String>();
    synchronized (interpreterSettingIds) {
      for (String id : interpreterSettingIds) {
        InterpreterSetting setting = factory.get(id);
        if (setting == null) {
          // interpreter setting is removed from factory. remove id from here, too
          interpreterSettingIds.remove(id);
        } else {
          settingIds.add(id);
        }
      }
    }
    return settingIds;
  }
  
  public List<InterpreterSetting> getInterpreterSettings() {
    LinkedList<InterpreterSetting> settings = new LinkedList<InterpreterSetting>();
    synchronized (interpreterSettingIds) {
      for (String id : interpreterSettingIds) {
        InterpreterSetting setting = factory.get(id);
        if (setting == null) {
          // interpreter setting is removed from factory. remove id from here, too
          interpreterSettingIds.remove(id);
        } else {
          settings.add(setting);
        }
      }
    }
    return settings;
  }

  public Interpreter get(String replName) {
    List<InterpreterSetting> settings = getInterpreterSettings();
    
    if (settings == null || settings.size() == 0) {
      return null;
    }
    
    if (replName == null) {
      return settings.get(0).getInterpreterGroup().getFirst();
    }
    
    String interpreterClassName = Interpreter.registeredInterpreters.get(replName).getClassName();
    if (interpreterClassName == null) {
      throw new InterpreterException(replName + " interpreter not found");
    }
    
    for (InterpreterSetting setting : settings) {
      InterpreterGroup intpGroup = setting.getInterpreterGroup();
      for (Interpreter interpreter : intpGroup) {
        if (interpreterClassName.equals(interpreter.getClassName())) {
          return interpreter;
        }
      }
    }
    
    return null;
  }
}
