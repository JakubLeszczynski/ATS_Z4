import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/*
W testach w pierwszej kolejności zamieniłem BeforAll na BeforeEach. Obiekt był tworzony tylko raz
dla wszystkich testów co nie do końca było dobre w kontekście JUnit-a, przez to, że nie mamy tutaj gwarantowanej kolejności wykonywania.
Zmiana z BeforeAll na BefioreEach pozwoliła na przygotowanie świeżego obiektu dla każdej jednostki testowej.
 */

public class GradebookTest {
    private static Gradebook gradebook;

    @BeforeEach
    public void setUp() {
        gradebook = new Gradebook();
    }

    @Test
    public void testAddSubject() {
        gradebook.addSubject("Physics");
        List<String> expectedList = List.of("Physics");
        assertEquals(expectedList, gradebook.getSubjects());
    }

    @Test
    public void testAddGradeToSubject() {
        gradebook.addSubject("Math");
        gradebook.addGrade("Math", 5.0);
        Map<String, List<Double>> expectedMap = new HashMap<>();
        expectedMap.put("Math", List.of(5.0));
        assertEquals(expectedMap, gradebook.getGrades());
    }

    /*
    Został wypełniony szkielet testów dynamicznych, wcześniej nie dodawał on żadnych ocen,
    przez co nie można było żadnych testów de facto zrobić.
    Dodałem oceny do każdego przedmiotu z wyjątkiem History ponieważ jest on wyłączony z użytkowania w klasie Gradebook.
     */


    @TestFactory
    Stream<DynamicTest> dynamicTestsForSubjects() {
        Map<String, List<Double>> testData = new HashMap<>();
        testData.put("Math", List.of(4.0, 5.0, 3.0));
        testData.put("Chemistry", List.of(4.0, 4.0, 3.0));
        testData.put("Biology", List.of(4.0, 5.0, 5.0));
        testData.put("Physics", List.of(4.0, 5.0, 2.0));

        testData.keySet().forEach(gradebook::addSubject);
        gradebook.addSubject("History");

        for (Map.Entry<String, List<Double>> entry : testData.entrySet()) {
            for (double grade : entry.getValue()) {
                gradebook.addGrade(entry.getKey(), grade);
            }
        }

        return testData.entrySet().stream()
                .map(entry -> DynamicTest.dynamicTest("Test for subject: " + entry.getKey(), () -> {
                    String subject = entry.getKey();
                    List<Double> grades = entry.getValue();

                    List<Double> actual = gradebook.getGrades().get(subject);
                    assertEquals(grades.size(), actual.size(), "Test amount of grades for subject: %s" + subject);
                    assertEquals(grades, actual, "Lista ocen dla przedmiotu: " + subject);

                    Double expectedAverage = Math.round(grades.stream().mapToDouble(Double::doubleValue).average().orElse(0) * 100.0) / 100.0;
                    double actualAverage = gradebook.calcAvgForSubject(subject);
                    assertEquals(expectedAverage, actualAverage, 0.001,
                            "Grade average test for subject: " + subject);
                }));
    }

    /*
     Testy poniżej generujną osobny test dla każdego przedmiotu i sprawdzają czy funkcja calcAvgForAllSubjects()
     zwraca poprawną średnią.
     */

    @TestFactory
    Stream<DynamicTest> dynamicTestForCalcAvgForAllSubjects() {
        Map<String, List<Double>> testData = new HashMap<>();
        testData.put("Math", List.of(4.0, 5.0, 3.0));
        testData.put("Chemistry", List.of(2.0, 5.0));
        testData.put("Biology", List.of(4.0, 5.0, 5.0, 3.0));

        testData.keySet().forEach(gradebook::addSubject);
        gradebook.addSubject("History");

        for (Map.Entry<String, List<Double>> entry : testData.entrySet()) {
            for (double grade : entry.getValue()) {
                gradebook.addGrade(entry.getKey(), grade);
            }
        }

        Map<String, Double> allAverages = gradebook.calcAvgForAllSubjects();

        return testData.entrySet().stream()
                .map(entry -> DynamicTest.dynamicTest(
                        "calcAvgForAllSubjects - subject: " + entry.getKey(), () -> {
                            String subject = entry.getKey();
                            double expectedAvg = Math.round(
                                    entry.getValue().stream()
                                            .mapToDouble(Double::doubleValue)
                                            .average().orElse(0) * 100.0
                            ) / 100.0;

                            assertTrue(allAverages.containsKey(subject),
                                    "The result should contain the Subject: " + subject);
                            assertEquals(expectedAvg, allAverages.get(subject), 0.001,
                                    "Wrong Average for subject: " + subject);
                        }));

    }
}