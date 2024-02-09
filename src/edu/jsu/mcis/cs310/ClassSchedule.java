package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
//import java.util.LinkedHashMap;

public class ClassSchedule {
    
    private final String CSV_FILENAME = "jsu_sp24_v1.csv";
    private final String JSON_FILENAME = "jsu_sp24_v1.json";
    
    private final String CRN_COL_HEADER = "crn";
    private final String SUBJECT_COL_HEADER = "subject";
    private final String NUM_COL_HEADER = "num";
    private final String DESCRIPTION_COL_HEADER = "description";
    private final String SECTION_COL_HEADER = "section";
    private final String TYPE_COL_HEADER = "type";
    private final String CREDITS_COL_HEADER = "credits";
    private final String START_COL_HEADER = "start";
    private final String END_COL_HEADER = "end";
    private final String DAYS_COL_HEADER = "days";
    private final String WHERE_COL_HEADER = "where";
    private final String SCHEDULE_COL_HEADER = "schedule";
    private final String INSTRUCTOR_COL_HEADER = "instructor";
    private final String SUBJECTID_COL_HEADER = "subjectid";
    
    /*
    public String convertCsvToJsonString(List<String[]> csv) throws FileNotFoundException {
        List<LinkedHashMap<String, Object>> sections = new ArrayList<>();
        LinkedHashMap<String, String> scheduleTypeMap = new LinkedHashMap<>();
        LinkedHashMap<String, String> subjectMap = new LinkedHashMap<>();
        LinkedHashMap<String, LinkedHashMap<String, Object>> courseMap = new LinkedHashMap<>(); // New map for courses

        Iterator<String[]> iterator = csv.iterator();
        String[] headers = iterator.next(); // Get headers

        while (iterator.hasNext()) {
            String[] row = iterator.next();

            LinkedHashMap<String, Object> section = new LinkedHashMap<>();
            for (int i = 0; i < headers.length; i++) {
                String key = headers[i];
                String value = row[i];
                switch (key) {
                    case CRN_COL_HEADER, SUBJECTID_COL_HEADER, NUM_COL_HEADER, SECTION_COL_HEADER, TYPE_COL_HEADER, START_COL_HEADER, END_COL_HEADER, DAYS_COL_HEADER, WHERE_COL_HEADER -> section.put(key, Integer.parseInt(value));
                    case INSTRUCTOR_COL_HEADER -> {
                        // Add instructor as a list
                        List<String> instructors = new ArrayList<>();
                        instructors.add(value);
                        section.put(key, instructors);
                    }
                    case SUBJECT_COL_HEADER -> {
                        // Populate subject map
                        String subjectId = row[i].split(" ")[0]; // Extract subject id
                        String subjectName = row[i].split(" ", 2)[1]; // Extract subject name
                        subjectMap.put(subjectId, subjectName);
                    }
                    case DESCRIPTION_COL_HEADER -> {
                        // Populate course map
                        String courseId = row[i]; // Extract course id
                        LinkedHashMap<String, Object> courseDetails = new LinkedHashMap<>();
                        courseDetails.put("subjectid", courseId.split(" ")[0]); // Extract subject id from course id
                        courseDetails.put("num", courseId.split(" ")[1]); // Extract course number from course id
                        courseDetails.put("description", value);
                        // Assuming credits are in the next column
                        courseDetails.put("credits", row[i + 1]);
                        courseMap.put(courseId, courseDetails);
                    }
                    case SCHEDULE_COL_HEADER -> {
                        // Extract and map schedule types
                        String type = (String) section.get(TYPE_COL_HEADER);
                        if (!scheduleTypeMap.containsKey(type)) {
                            scheduleTypeMap.put(type, value);
                        }
                    }
                }
            }

        sections.add(section);
    }

    LinkedHashMap<String, Object> jsonObject = new LinkedHashMap<>();
    jsonObject.put("scheduletype", scheduleTypeMap);
    jsonObject.put("subject", subjectMap); // Add subject map to JSON object
    jsonObject.put("course", courseMap); // Add course map to JSON object
    jsonObject.put("section", sections);
    
    String jsonString = Jsoner.serialize(jsonObject);
        
    return jsonString;
}
    */
    
    
    public String convertCsvToJsonString(List<String[]> csv) {
        // initializing jsonObjects for different parts
        JsonObject json = new JsonObject();
        JsonObject scheduletypeMap = new JsonObject();
        JsonObject subjectMap = new JsonObject();
        JsonObject courseMap = new JsonObject();
        JsonArray sectionList = new JsonArray();

        // iterator for csv rows
        Iterator<String[]> iterator = csv.iterator();

        // header row
        String[] headers = iterator.next();

        // mapping headers with their respective indices
        HashMap<String, Integer> dataList = new HashMap<>();
        for (int i = 0; i < headers.length; ++i) {
            dataList.put(headers[i], i);
        }

        String jsonString = null;

        // loop through csv rows
        while (iterator.hasNext()) {
            String[] row = iterator.next();
        
            // retrieve data from each row
            String subject = row[dataList.get(SUBJECT_COL_HEADER)];
            String num = row[dataList.get(NUM_COL_HEADER)];
            String description = row[dataList.get(DESCRIPTION_COL_HEADER)];
            String section = row[dataList.get(SECTION_COL_HEADER)];
            String type = row[dataList.get(TYPE_COL_HEADER)];
            String start = row[dataList.get(START_COL_HEADER)];
            String end = row[dataList.get(END_COL_HEADER)];
            String days = row[dataList.get(DAYS_COL_HEADER)];
            String where = row[dataList.get(WHERE_COL_HEADER)];
            String schedule = row[dataList.get(SCHEDULE_COL_HEADER)];
            String instructor = row[dataList.get(INSTRUCTOR_COL_HEADER)]; 
            
            Integer crn = Integer.valueOf(row[dataList.get(CRN_COL_HEADER)]);
            Integer credits = Integer.valueOf(row[dataList.get(CREDITS_COL_HEADER)]);
            
            String[] course = num.split("\\s+", 2);
            String courseId = course[0]; // course id letters 
            String courseNum = course.length > 1 ? course[1] : ""; // course id number

            // populating courseMap
            JsonObject addCourse = new JsonObject();
            addCourse.put(SUBJECTID_COL_HEADER, courseId);
            addCourse.put(NUM_COL_HEADER, courseNum);
            addCourse.put(DESCRIPTION_COL_HEADER, description);
            addCourse.put(CREDITS_COL_HEADER, credits);

            courseMap.put(num,addCourse);

            // split list of instructors
            List<String> instructors = Arrays.asList(instructor.split(", "));

            // add sections to object
            JsonObject addSections = new JsonObject();
            addSections.put(CRN_COL_HEADER, crn);
            addSections.put(SUBJECTID_COL_HEADER, courseId);
            addSections.put(NUM_COL_HEADER, courseNum);
            addSections.put(SECTION_COL_HEADER, section);
            addSections.put(TYPE_COL_HEADER, type);
            addSections.put(START_COL_HEADER, start);
            addSections.put(END_COL_HEADER, end);
            addSections.put(DAYS_COL_HEADER, days);
            addSections.put(WHERE_COL_HEADER, where);
            addSections.put(INSTRUCTOR_COL_HEADER, instructors);

            // add objects to respective json objects
            sectionList.add(addSections);  
            scheduletypeMap.put(type, schedule);
            subjectMap.put(courseId, subject);
        
            // add element to json object
            json.put("scheduletype", scheduletypeMap);
            json.put("subject", subjectMap);
            json.put("course", courseMap);
            json.put("section", sectionList);
        
            jsonString = Jsoner.serialize(json);
        }
        return jsonString;
    }

    public String convertJsonToCsvString(JsonObject json) {
      
        // getting jsonObjects 
        JsonObject scheduletype = (JsonObject) json.get("scheduletype");
        JsonObject subjects = (JsonObject) json.get("subject");
        JsonObject courses = (JsonObject) json.get("course");
        JsonArray sections = (JsonArray) json.get("section");

        // holding csv file as string
        String csvString;
    
        // initializing StringWriter
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
    
        // writing csv header
        csvWriter.writeNext(new String[]{CRN_COL_HEADER, SUBJECT_COL_HEADER, NUM_COL_HEADER, DESCRIPTION_COL_HEADER, SECTION_COL_HEADER, TYPE_COL_HEADER, CREDITS_COL_HEADER, START_COL_HEADER, END_COL_HEADER, DAYS_COL_HEADER, WHERE_COL_HEADER, SCHEDULE_COL_HEADER, INSTRUCTOR_COL_HEADER});
    
        // interator for sections
        Iterator<Object> iterator = sections.iterator();
        
        // extracting section data
        while (iterator.hasNext()) {
            JsonObject section = (JsonObject) iterator.next();
            String crn = String.valueOf(section.get(CRN_COL_HEADER));
            String subjectID = (String) section.get(SUBJECTID_COL_HEADER);
            String subject = (String) subjects.get(subjectID);
            String courseNum = (String) section.get(NUM_COL_HEADER);
            String num = subjectID + " " + courseNum;
            JsonObject course = (JsonObject) courses.get(num);
        
            String description = (String) course.get(DESCRIPTION_COL_HEADER);
            String sectionId = (String) section.get(SECTION_COL_HEADER);
            String type = (String) section.get(TYPE_COL_HEADER);
            BigDecimal creditsValue = (BigDecimal) course.get(CREDITS_COL_HEADER);
            String credits = creditsValue.toString();
            String start = (String) section.get(START_COL_HEADER);
            String end = (String) section.get(END_COL_HEADER);
            String days = (String) section.get(DAYS_COL_HEADER);
            String where = (String) section.get(WHERE_COL_HEADER);
            String schedule = (String) scheduletype.get(type); 
            JsonArray instructorsArray = (JsonArray) section.get(INSTRUCTOR_COL_HEADER);
        
            List<String> instructorsList = new ArrayList<>();
            Iterator<Object> instructorIterator = instructorsArray.iterator();
            while (instructorIterator.hasNext()) {
                instructorsList.add(instructorIterator.next().toString());
            }
        
            // joining instructorsList
            String instructor = String.join(", ", instructorsList);
        
            // writign csv row
            csvWriter.writeNext(new String[]{crn, subject, num, description, sectionId, type, credits, start, end, days, where, schedule, instructor});
        }
    
        csvString = writer.toString();

        return csvString;
    }


 
    public JsonObject getJson() {
        
        JsonObject json = getJson(getInputFileData(JSON_FILENAME));
        return json;
        
    }
    
    public JsonObject getJson(String input) {
        
        JsonObject json = null;
        
        try {
            json = (JsonObject)Jsoner.deserialize(input);
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return json;
        
    }
    
    public List<String[]> getCsv() {
        
        List<String[]> csv = getCsv(getInputFileData(CSV_FILENAME));
        return csv;
        
    }
    
    public List<String[]> getCsv(String input) {
        
        List<String[]> csv = null;
        
        try {
            
            CSVReader reader = new CSVReaderBuilder(new StringReader(input)).withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build();
            csv = reader.readAll();
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return csv;
        
    }
    
    public String getCsvString(List<String[]> csv) {
        
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        
        csvWriter.writeAll(csv);
        
        return writer.toString();
        
    }
    
    private String getInputFileData(String filename) {
        
        StringBuilder buffer = new StringBuilder();
        String line;
        
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        
        try {
        
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("resources" + File.separator + filename)));

            while((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return buffer.toString();
        
    }
    
}