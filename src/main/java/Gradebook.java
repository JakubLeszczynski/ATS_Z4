import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Gradebook {
    private final List<String> subjects;
    private final Map<String, List<Double>> subjectsGrades;

    public Gradebook() {
        this.subjects = new ArrayList<>();
        this.subjectsGrades = new HashMap<>();
    }

    public void addSubject(String subject) {
        subjects.add(subject);
        subjectsGrades.put(subject, new ArrayList<>());
    }

    public void addGrade(String subject, double grade) {
        if (subjectsGrades.containsKey(subject)) {
            if (!subject.equals("History")) {
                subjectsGrades.get(subject).add(grade);
            }
        } else {
            throw new IllegalArgumentException(subject + " not found in list of subjects");
        }
    }

    public double calcAvgForSubject(String subject) {
        if (subjectsGrades.containsKey(subject)) {
            List<Double> grades = subjectsGrades.get(subject);
            double subjectGradeSum = grades.stream().mapToDouble(Double::doubleValue).sum();
            int subjectGradeCount = grades.size();
            if (subjectGradeCount > 0) {
                return Math.round((subjectGradeSum / subjectGradeCount) * 100.0) / 100.0;
            } else {
                throw new IllegalArgumentException("No grades found for subject");
            }
        } else {
            throw new IllegalArgumentException("Subject not in subjects");
        }
    }
    /*
    Metoda calcAvgForAllSubject oblicza srednia ze wszystkich ocen z każdego przedmiotu,
    ale ignoruje przedmioty do których sie nie da dodać ocen, czyli History.
    Jeżeli żaden przedmiot nie ma ocen to rzuca IllegalArgumentException.
    */
    public Map<String, Double> calcAvgForAllSubjects() {
//TODO - Zaimplementuj i przetestuj tą metodę
        Map<String, Double> result = new HashMap<>();

        for (Map.Entry<String, List<Double>> entry : subjectsGrades.entrySet()) {
            String subject = entry.getKey();
            List<Double> grades = entry.getValue();

            if (!grades.isEmpty()) {
                double sum  = grades.stream().mapToDouble(Double::doubleValue).sum();
                double avg = (double) Math.round((sum / grades.size()) * 100.0) / 100;
                result.put(subject, avg);
                }

            }
        return result;
    }


    public List<String> getSubjects() {
        return subjects;
    }

    public Map<String, List<Double>> getGrades() {
        return subjectsGrades;
    }
}