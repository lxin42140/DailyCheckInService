package com.example.demo;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;

import org.springframework.web.bind.annotation.*;

@RestController
public class CheckInSurveyController {
    public Iterable<DataSnapshot> surveyListSnapshot;
    public HashMap<String, Survey> surveyMap;

    public Iterable<DataSnapshot> answerListSnapshot;

    InputStream serviceAccount = this.getClass().getClassLoader()
            .getResourceAsStream("project-fb-7ec80-firebase-adminsdk-v0df1-14c68b1fc7.json");
    FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("https://project-fb-7ec80-default-rtdb.asia-southeast1.firebasedatabase.app").build();
    FirebaseApp app = FirebaseApp.initializeApp(options);
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference surveyRef = database.getReference("survey");

    DatabaseReference answerRef = database.getReference("answers");

    public CheckInSurveyController() throws IOException {
        surveyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CheckInSurveyController.this.surveyListSnapshot = dataSnapshot.getChildren();

                if (CheckInSurveyController.this.surveyMap == null) {
                    CheckInSurveyController.this.surveyMap = new HashMap<>();
                }

                for (DataSnapshot d : CheckInSurveyController.this.surveyListSnapshot) {
                    CheckInSurveyController.this.surveyMap.put(d.getKey(), d.getValue(Survey.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        answerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CheckInSurveyController.this.answerListSnapshot = dataSnapshot.getChildren();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @RequestMapping("/getCheckInSurveyForCurrentDate")
    public Survey getCheckInSurveyForCurrentDate() {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(new Date());

        for (Map.Entry<String, Survey> e : this.surveyMap.entrySet()) {
            cal2.setTime(e.getValue().dateCreated);
            if (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
                    && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                return this.surveyMap.get(e.getKey());
            }
        }
        return null;
    }

    @RequestMapping("/getCheckInSurveys")
    public HashMap<String, Survey> getCheckInSurveys() {
        return this.surveyMap;
    }

    @RequestMapping("/addCheckInSurvey")
    public String addCheckInSurvey(@RequestBody Survey survey) {
        DatabaseReference postSurvey = surveyRef.push();
        postSurvey.setValueAsync(survey);
        return survey.toString();
    }

    @RequestMapping("/updateCheckInSurvey")
    public String updateCheckInSurvey(@RequestParam String surveyKey, @RequestBody Survey updatedSurvey) {
        HashMap<String, Object> temp = new HashMap<>();
        temp.put(surveyKey, updatedSurvey);
        surveyRef.updateChildrenAsync(temp);
        return updatedSurvey.toString();
    }

    @RequestMapping("/answerCheckInSurvey")
    public String answerCheckInSurvey(@RequestParam String employeeNumber, @RequestBody Survey surveyAnswer) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String today = formatter.format(new Date());
        DatabaseReference ansRef = answerRef.child(today.replace("/", "")).child(employeeNumber).push();

        int score = 0;
        for (Question q : surveyAnswer.questions) {
            for (QuestionOption o : q.options) {
                if (o.isSelected != null && o.isSelected) {
                    score += o.optionScore;
                }
            }
        }
        surveyAnswer.score = score;

        ansRef.setValueAsync(surveyAnswer);
        return employeeNumber;
    }

    @RequestMapping("/getAnswersForLatestCheckInSurvey")
    public HashMap<String, Survey> getAnswersForLatestCheckInSurvey() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String today = formatter.format(new Date());

        HashMap<String, Survey> map = new HashMap<>();

        for (DataSnapshot date : this.answerListSnapshot) {
            if (date.getKey().equals(today.replace("/", ""))) {
                for (DataSnapshot employee : date.getChildren()) {
                    for (DataSnapshot answer : employee.getChildren()) {
                        map.put(employee.getKey(), answer.getValue(Survey.class));
                    }
                }
            }
        }

        return map;
    }
}
