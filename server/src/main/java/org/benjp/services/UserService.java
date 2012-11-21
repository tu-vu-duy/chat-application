package org.benjp.services;

import com.mongodb.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named("userService")
@ApplicationScoped
public class UserService
{

  private static final String M_SESSIONS_COLLECTION = "sessions";
  private static final String M_USERS_COLLECTION = "users";
  private static final String M_SPACES_COLLECTION = "spaces";

  public static final String STATUS_AVAILABLE = "available";
  public static final String STATUS_DONOTDISTURB = "donotdisturb";
  public static final String STATUS_AWAY = "away";
  public static final String STATUS_INVISIBLE = "invisible";
  public static final String STATUS_NONE = "none";
  public static final String STATUS_SPACE = "space";


  private DB db()
  {
    return MongoBootstrap.getDB();
  }


  public boolean hasSession(String session)
  {
    DBCollection coll = db().getCollection(M_SESSIONS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("session", session);
    DBCursor cursor = coll.find(query);
    return (cursor.hasNext());
  }

  public boolean hasUser(String user)
  {
    DBCollection coll = db().getCollection(M_SESSIONS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("user", user);
    DBCursor cursor = coll.find(query);
    return (cursor.hasNext());
  }

  public boolean hasUserWithSession(String user, String session)
  {
    DBCollection coll = db().getCollection(M_SESSIONS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("user", user);
    query.put("session", session);
    DBCursor cursor = coll.find(query);
    //System.out.println("hasUserWithSession Size = "+cursor.size()+" - "+cursor.hasNext()+" - "+user+" ; "+session);
    return (cursor.hasNext());
  }

  public void addUser(String user, String session)
  {
    if (!hasUserWithSession(user, session))
    {
      System.out.println("USER SERVICE :: ADDING :: " + user + " : " + session);
      removeUser(user);
      DBCollection coll = db().getCollection(M_SESSIONS_COLLECTION);

      BasicDBObject doc = new BasicDBObject();
      doc.put("_id", session);
      doc.put("user", user);
      doc.put("session", session);

      coll.insert(doc);
    }
  }

  public void toggleFavorite(String user, String targetUser)
  {
    DBCollection coll = db().getCollection(M_USERS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("user", user);
    DBCursor cursor = coll.find(query);
    if (cursor.hasNext())
    {
      DBObject doc = cursor.next();
      List<String> favorites = new ArrayList<String>();
      if (doc.containsField("favorites")) {
        favorites = (List<String>)doc.get("favorites");
      }
      if (favorites.contains(targetUser))
        favorites.remove(targetUser);
      else
        favorites.add(targetUser);

      doc.put("favorites", favorites);
      coll.save(doc, WriteConcern.SAFE);
    }
  }

  public boolean isFavorite(String user, String targetUser)
  {
    DBCollection coll = db().getCollection(M_USERS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("user", user);
    DBCursor cursor = coll.find(query);
    if (cursor.hasNext())
    {
      DBObject doc = cursor.next();
      if (doc.containsField("favorites")) {
        List<String> favorites = (List<String>)doc.get("favorites");
        if (favorites.contains(targetUser))
          return true;
      }
    }
    return false;
  }

  public void addUserFullName(String user, String fullname)
  {
    DBCollection coll = db().getCollection(M_USERS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("user", user);
    DBCursor cursor = coll.find(query);
    if (!cursor.hasNext())
    {
      BasicDBObject doc = new BasicDBObject();
      doc.put("user", user);
      doc.put("fullname", fullname);
      coll.insert(doc);
    }
  }

  public void setSpaces(String user, List<SpaceBean> spaces)
  {
    List<String> spaceIds = new ArrayList<String>();
    DBCollection coll = db().getCollection(M_SPACES_COLLECTION);
    for (SpaceBean bean:spaces)
    {
      spaceIds.add(bean.getId());

      BasicDBObject query = new BasicDBObject();
      query.put("_id", bean.getId());
      DBCursor cursor = coll.find(query);
      if (!cursor.hasNext())
      {
        BasicDBObject doc = new BasicDBObject();
        doc.put("_id", bean.getId());
        doc.put("displayName", bean.getDisplayName());
        doc.put("groupId", bean.getGroupId());
        doc.put("shortName", bean.getShortName());
        coll.insert(doc);
      }
      else
      {
        DBObject doc = cursor.next();
        doc.put("_id", bean.getId());
        doc.put("displayName", bean.getDisplayName());
        doc.put("groupId", bean.getGroupId());
        doc.put("shortName", bean.getShortName());
        coll.save(doc);
      }


    }
    coll = db().getCollection(M_USERS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("user", user);
    DBCursor cursor = coll.find(query);
    if (cursor.hasNext())
    {
      DBObject doc = cursor.next();
      doc.put("spaces", spaceIds);
      coll.save(doc, WriteConcern.SAFE);
    }
    else
    {
      BasicDBObject doc = new BasicDBObject();
      doc.put("user", user);
      doc.put("spaces", spaceIds);
      coll.insert(doc);
    }
  }

  private SpaceBean getSpace(String spaceId)
  {
    SpaceBean spaceBean = null;
    DBCollection coll = db().getCollection(M_SPACES_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("_id", spaceId);
    DBCursor cursor = coll.find(query);
    if (cursor.hasNext())
    {
      DBObject doc = cursor.next();
      spaceBean = new SpaceBean();
      spaceBean.setId(spaceId);
      spaceBean.setDisplayName(doc.get("displayName").toString());
      spaceBean.setGroupId(doc.get("groupId").toString());
      spaceBean.setShortName(doc.get("shortName").toString());
    }

    return spaceBean;
  }

  public List<SpaceBean> getSpaces(String user)
  {
    List<SpaceBean> spaces = new ArrayList<SpaceBean>();
    DBCollection coll = db().getCollection(M_USERS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("user", user);
    DBCursor cursor = coll.find(query);
    if (cursor.hasNext())
    {
      DBObject doc = cursor.next();

      List<String> listspaces = ((List<String>)doc.get("spaces"));
      for (String space:listspaces)
      {
        spaces.add(getSpace(space));
      }

    }
    return spaces;
  }

  public String setStatus(String user, String status)
  {
    DBCollection coll = db().getCollection(M_USERS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("user", user);
    DBCursor cursor = coll.find(query);
    if (cursor.hasNext())
    {
      DBObject doc = cursor.next();
      doc.put("status", status);
      coll.save(doc, WriteConcern.SAFE);
    }
    else
    {
      BasicDBObject doc = new BasicDBObject();
      doc.put("user", user);
      doc.put("status", status);
      coll.insert(doc);
    }
    return status;
  }

  public String getStatus(String user)
  {
    String status = STATUS_NONE;
    DBCollection coll = db().getCollection(M_USERS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("user", user);
    DBCursor cursor = coll.find(query);
    if (cursor.hasNext())
    {
      DBObject doc = cursor.next();
      if (doc.containsField("status"))
        status = doc.get("status").toString();
      else
        status = setStatus(user, STATUS_AVAILABLE);
    }
    else
    {
      status = setStatus(user, STATUS_AVAILABLE);
    }

    return status;
  }

  public String getUserFullName(String user)
  {
    String fullname = null;
    DBCollection coll = db().getCollection(M_USERS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("user", user);
    DBCursor cursor = coll.find(query);
    if (cursor.hasNext())
    {
      DBObject doc = cursor.next();
      fullname = doc.get("fullname").toString();
    }

    return fullname;
  }

  public void removeSession(String session)
  {
    DBCollection coll = db().getCollection(M_SESSIONS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("session", session);
    DBCursor cursor = coll.find(query);
    while (cursor.hasNext())
    {
      DBObject doc = cursor.next();
      String user = doc.get("user").toString();
      System.out.println("USER SERVICE :: REMOVING :: " + user + " : " + session);
      coll.remove(doc);
    }
  }

  private void removeUser(String user)
  {
    DBCollection coll = db().getCollection(M_SESSIONS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("user", user);
    DBCursor cursor = coll.find(query);
    while (cursor.hasNext())
    {
      DBObject doc = cursor.next();
      coll.remove(doc);
    }
  }

  public List<String> getUsers()
  {
    ArrayList<String> users = new ArrayList<String>();
    DBCollection coll = db().getCollection(M_SESSIONS_COLLECTION);
    DBCursor cursor = coll.find();
    while (cursor.hasNext())
    {
      DBObject doc = cursor.next();
      users.add(doc.get("user").toString());
    }

    return users;
  }

  public List<String> getUsersFilterBy(String user)
  {
    ArrayList<String> users = new ArrayList<String>();
    DBCollection coll = db().getCollection(M_SESSIONS_COLLECTION);
    DBCursor cursor = coll.find();
    while (cursor.hasNext())
    {
      DBObject doc = cursor.next();
      String target = doc.get("user").toString();
      if (!user.equals(target))
        users.add(target);
    }

    return users;
  }

  public List<String> getUsersFilterBy(String user, String space)
  {
    ArrayList<String> users = new ArrayList<String>();
    DBCollection coll = db().getCollection(M_USERS_COLLECTION);
    BasicDBObject query = new BasicDBObject();
    query.put("spaces", space);
    DBCursor cursor = coll.find(query);
    while (cursor.hasNext())
    {
      DBObject doc = cursor.next();
      String target = doc.get("user").toString();
      if (!user.equals(target))
        users.add(target);
    }

    return users;
  }


}
