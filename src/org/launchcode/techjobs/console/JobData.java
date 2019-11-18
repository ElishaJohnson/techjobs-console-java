package org.launchcode.techjobs.console;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LaunchCode
 */
public class JobData {

    private static final String DATA_FILE = "resources/job_data.csv";
    private static Boolean isDataLoaded = false;

    private static ArrayList<HashMap<String, String>> allJobs;

    /**
     * Fetch list of all values from loaded data,
     * without duplicates, for a given column.
     *
     * @param field The column to retrieve values from
     * @return List of all of the values of the given field
     */
    public static ArrayList<String> findAll(String field) {

        // load data, if not already loaded
        loadData();

        ArrayList<String> values = new ArrayList<>();

        for (HashMap<String, String> row : allJobs) {
            String aValue = row.get(field);

            if (!values.contains(aValue)) {
                values.add(aValue);
            }
        }

        return values;
    }

    public static ArrayList<HashMap<String, String>> findAll() {

        // load data, if not already loaded
        loadData();
        ArrayList<HashMap<String, String>> allJobsCopy = new ArrayList<>();
        for (HashMap<String, String> row : allJobs) {
            allJobsCopy.add(hashCopy(row));
        }
        return fullSort(allJobsCopy);
    }

    /**
     * Returns results of search the jobs data by key/value, using
     * inclusion of the search term.
     *
     * For example, searching for employer "Enterprise" will include results
     * with "Enterprise Holdings, Inc".
     *
     * @param column   Column that should be searched.
     * @param value Value of teh field to search for
     * @return List of all jobs matching the criteria
     */
    public static ArrayList<HashMap<String, String>> findByColumnAndValue(String column, String value) {

        // load data, if not already loaded
        loadData();

        ArrayList<HashMap<String, String>> jobs = new ArrayList<>();

        for (HashMap<String, String> row : allJobs) {

            String aValue = row.get(column);

            if (aValue.toLowerCase().contains(value.toLowerCase())) {
                jobs.add(row);
            }
        }

        jobs = fullSort(jobs);
        jobs = sortByKey(jobs, column);

        return jobs;
    }

    public static ArrayList<HashMap<String, String>> findByValue(String value) {
        // load data, if not already loaded
        loadData();
        ArrayList<HashMap<String, String>> jobs = new ArrayList<>();
        for (HashMap<String, String> row : allJobs) {
            ArrayList<String> rowEntry = new ArrayList<>(row.keySet());
            for (String column : rowEntry) {
               String aValue = row.get(column);
               if (aValue.toLowerCase().contains(value.toLowerCase()) && !jobs.contains(row)) {
                   jobs.add(row);
               }
            }
        }
        return fullSort(jobs);
    }

    private static ArrayList<HashMap<String, String>> fullSort(ArrayList<HashMap<String, String>> jobs) {
        jobs = sortByKey(jobs, "name");
        jobs = sortByKey(jobs, "position type");
        jobs = sortByKey(jobs, "core competency");
        jobs = sortByKey(jobs, "employer");
        jobs = sortByKey(jobs, "location");
        return jobs;
    }

    private static ArrayList<HashMap<String, String>> sortByKey(ArrayList<HashMap<String, String>> jobs, String key) {
        boolean sorted = false;
        int range = jobs.size() - 1;
        do {
            sorted = true;
            for (int i = 0; i < range; i++) {
                int comparison = jobs.get(i).get(key).compareToIgnoreCase(jobs.get(i + 1).get(key));
                if (comparison > 0) {
                    sorted = false;
                    HashMap<String, String> temp = hashCopy(jobs.get(i));
                    jobs.set(i, jobs.get(i + 1));
                    jobs.set(i + 1, temp);
                }
            }
            range--;
        } while (!sorted);
        return jobs;
    }

    private static HashMap<String, String> hashCopy(HashMap<String, String> mapToCopy) {
        HashMap<String, String> mapCopy = new HashMap<>();
        ArrayList<Map.Entry<String, String>> list = new ArrayList(mapToCopy.entrySet());
        for (Map.Entry<String, String> element : list) {
            mapCopy.put(element.getKey(), element.getValue());
        }
        return mapCopy;
    }

    /**
     * Read in data from a CSV file and store it in a list
     */
    private static void loadData() {

        // Only load data once
        if (isDataLoaded) {
            return;
        }

        try {

            // Open the CSV file and set up pull out column header info and records
            Reader in = new FileReader(DATA_FILE);
            CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            List<CSVRecord> records = parser.getRecords();
            Integer numberOfColumns = records.get(0).size();
            String[] headers = parser.getHeaderMap().keySet().toArray(new String[numberOfColumns]);

            allJobs = new ArrayList<>();

            // Put the records into a more friendly format
            for (CSVRecord record : records) {
                HashMap<String, String> newJob = new HashMap<>();

                for (String headerLabel : headers) {
                    newJob.put(headerLabel, record.get(headerLabel));
                }

                allJobs.add(newJob);
            }

            // flag the data as loaded, so we don't do it twice
            isDataLoaded = true;

        } catch (IOException e) {
            System.out.println("Failed to load job data");
            e.printStackTrace();
        }
    }

}
