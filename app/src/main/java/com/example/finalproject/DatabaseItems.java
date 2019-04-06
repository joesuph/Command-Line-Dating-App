package com.example.finalproject;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * This class handles all database queries.
 */
public class DatabaseItems {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userDoc;

    private String zipcode = null;
    private Long userId = null;

    public String getZipcode() {
        return zipcode;
    }

    public Long getUserId() {
        return userId;
    }



    public boolean checkEmail(String email){
        DocumentReference emailCheck = db.document("Emails/" + email);

        //Get the format for reading
        Task<DocumentSnapshot> task = emailCheck.get();

        //Wait for task to finish, this process cannot be ignored and
        //ran asynchronously
        DocumentSnapshot dEmail = null;
        try {
            dEmail = Tasks.await(task);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return dEmail.exists();
    }

    public boolean getAccountInfo(String email, String password){
        DocumentReference userDocRef = db.document("emails_passwords/" + email + "_" + password);
        Task<DocumentSnapshot> task1 = userDocRef.get();

        //Wait for this operation to be complete, do not run asynchronously
        DocumentSnapshot userDocSnap = null;
        try {
            userDocSnap = Tasks.await(task1);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (userDocSnap.exists()){
            zipcode = (String)userDocSnap.get("zipcode");
            userId = (Long)userDocSnap.get("userId");
            return true;
        }
        return false;
    }


    public void createProfile(String email, String password, String name, String zipcode, String description, String contactInfo){

        //get userId
        this.zipcode = zipcode;
        System.out.println("log man: Accessing usercount Doc");
        DocumentReference userCountDoc = db.document("usercount/usercount");
        Task<DocumentSnapshot> task1 = userCountDoc.get();
        DocumentSnapshot userCountSnap = null;
        try {
            userCountSnap = Tasks.await(task1);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("log man: Setting userId to usercount");
        userId = (Long)userCountSnap.get("usercount");
        System.out.println("log man: Updating usercount");
        userCountDoc.update("usercount",(Long)(userId + 1));

        System.out.println("log man: opening zipdoc");
        //check if zip exists
        DocumentReference zipcodeDoc = db.document("zipcodes/" + zipcode);
        Task<DocumentSnapshot> task2 = zipcodeDoc.get();
        DocumentSnapshot zipcodeDocSnap = null;
        try {
            zipcodeDocSnap = Tasks.await(task2);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Add user Data to zipcode
        System.out.println("log man: creating user object");
        Map<String,Object> userData = new HashMap<String, Object>();
        userData.put("name",name);
        userData.put("description",description);
        userData.put("contactInfo",contactInfo);
        Map<String,Object> userDataContainer = new HashMap<String, Object>();
        userDataContainer.put(userId.toString(),userData);

        System.out.println("log man: checking if zipcode is already in system");
        if(zipcodeDocSnap.exists()) {
            System.out.println("log man: zipcode in system, updating zipcode doc to contain user");
            zipcodeDoc.update(userId.toString(),userData);
        }
        else
        {
            System.out.println("log man: zip not in system, creating zip doc with user");
            CollectionReference zipcodesCollection = db.collection("zipcodes");
            zipcodesCollection.document(zipcode).set(userDataContainer);
        }
        System.out.println("log man: User should be added to zipcode");

        //Add user data to email_password
        userData = new HashMap<String, Object>();
        userData.put("zipcode",zipcode);
        userData.put("userId", userId);
        db.document("emails_passwords/" + email + "_" + password).set(userData);

        //Add user to matches
        userData = new HashMap<String, Object>();
        db.document("matches/" + userId.toString()).set(userData);

    }

    public  ArrayList<Map<String, Object>> getProfiles(){
        Task<DocumentSnapshot> task1 = db.document("zipcodes/" + zipcode).get();

        //Wait for this operation to be complete, do not run asynchronously
        DocumentSnapshot zipSnap = null;
        try {
            zipSnap = Tasks.await(task1);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<String, Map<String,Object>> profileMap = (Map)zipSnap.getData();
        profileMap.remove(userId.toString());
        Map<String,Object> m1;
        ArrayList<Map<String,Object>> a = new ArrayList<Map<String,Object>>();
        for (Map.Entry<String,Map<String,Object>> entry : profileMap.entrySet())
        {
            m1 = entry.getValue();
            m1.put("userId",Long.valueOf(entry.getKey()));
            a.add(m1);
        }

        return a;
    }

    public void addLike(Long likedId){
        Task<DocumentSnapshot> task = db.document("matches/" + userId.toString()).get();
        DocumentSnapshot matchSnap = null;
        try {
            matchSnap = Tasks.await(task);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<Long> a;
        ArrayList<Long> likesList= (ArrayList<Long>)matchSnap.get("likes");
        if (likesList != null)
            a = likesList;
        else
            a = new ArrayList<Long>();
        if (!a.contains(likedId))
            a.add(likedId);
        db.document("matches/" + userId.toString()).update("likes",a);
    }

    public Boolean checkLikes(Long likedUserId){
        Task<DocumentSnapshot> task = db.document("matches/" + likedUserId.toString()).get();
        DocumentSnapshot likeSnap = null;
        try {
            likeSnap = Tasks.await(task);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<Long> likesList = (ArrayList<Long>)likeSnap.get("likes");
        if (likesList == null)
            return false;
        for(int i=0;i<likesList.size();i++){
            if (likesList.get(i) == userId)
                return true;
        }
        return false;
    }

    public void match(Long likedUserId){
        Task<DocumentSnapshot> task = db.document("matches/" + userId.toString()).get();
        Task<DocumentSnapshot> task2 = db.document("matches/" + likedUserId.toString()).get();
        DocumentSnapshot userMatchSnap = null;
        DocumentSnapshot likedUserSnap = null;
        try {
            userMatchSnap = Tasks.await(task);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            likedUserSnap = Tasks.await(task2);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<Long> a;
        ArrayList<Long> b;
        ArrayList<Long> c;
        ArrayList<Long> d;
        ArrayList<Long> userLikesList= (ArrayList<Long>)userMatchSnap.get("matches");
        ArrayList<Long> likedLikesList= (ArrayList<Long>)likedUserSnap.get("matches");
        ArrayList<Long> userNewMatches = (ArrayList<Long>)userMatchSnap.get("newmatches");
        ArrayList<Long> likedNewMatches = (ArrayList<Long>)likedUserSnap.get("newmatches");

        if (userLikesList != null)
            a = userLikesList;
        else
            a = new ArrayList<Long>();

        if (likedLikesList != null)
            b = likedLikesList;
        else
            b = new ArrayList<Long>();

        if (userNewMatches != null)
            c = userNewMatches;
        else
            c = new ArrayList<Long>();

        if (likedNewMatches != null)
            d = likedNewMatches;
        else
            d = new ArrayList<Long>();

        if (a.contains(likedUserId))
            return;
        a.add(likedUserId);
        b.add(userId);
        c.add(likedUserId);
        d.add(userId);
        db.document("matches/" + userId.toString()).update("matches",a);
        db.document("matches/" + likedUserId.toString()).update("matches",b);
        db.document("matches/" + userId.toString()).update("newmatches",c);
        db.document("matches/" + likedUserId.toString()).update("newmatches",d);

    }

    public ArrayList<Map<String,Object>> getMatches(){
        ArrayList<Map<String,Object>> a = new ArrayList<Map<String,Object>>();
        Task<DocumentSnapshot> task = db.document("matches/" + userId.toString()).get();
        DocumentSnapshot matchSnap = null;
        try {
            matchSnap = Tasks.await(task);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<Long> l = (ArrayList<Long>)matchSnap.get("matches");
        if (l == null)return new ArrayList<Map<String,Object>>();
        Task<DocumentSnapshot> task2 = db.document("zipcodes/" + zipcode).get();
        DocumentSnapshot zipSnap = null;
        try {
            zipSnap = Tasks.await(task2);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i=0;i<l.size();i++){ a.add((Map<String, Object>) zipSnap.get(l.get(i).toString())); }

        return a;
    }

    public ArrayList getNewMatches(){
        ArrayList<Map<String,Object>> a = new ArrayList<Map<String,Object>>();
        Task<DocumentSnapshot> task = db.document("matches/" + userId.toString()).get();
        DocumentSnapshot matchSnap = null;
        try {
            matchSnap = Tasks.await(task);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<Long> l = (ArrayList<Long>)matchSnap.get("newmatches");
        if (l == null)return new ArrayList<Map<String,Object>>();
        Task<DocumentSnapshot> task2 = db.document("zipcodes/" + zipcode).get();
        DocumentSnapshot zipSnap = null;
        try {
            zipSnap = Tasks.await(task2);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i=0;i<l.size();i++){ a.add((Map<String, Object>) zipSnap.get(l.get(i).toString())); }
        db.document("matches/" + userId.toString()).update("newmatches",new ArrayList<Long>());
        return a;
    }






}
