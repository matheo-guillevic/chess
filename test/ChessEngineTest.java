/**
 * Lance toutes les suites de tests sans dependance externe.
 */
public class ChessEngineTest {
    /**
     * Point d'entree du runner de tests.
     *
     * @param args arguments ignores
     */
    public static void main(String[] args) {
        TestSupport support = new TestSupport();
        TestSuite[] suites = {
                new BasicRulesTest(),
                new SpecialMovesTest(),
                new JsonPiecesTest(),
                new AiTest()
        };

        for (TestSuite suite : suites) {
            System.out.println("\n== " + suite.getClass().getSimpleName() + " ==");
            suite.run(support);
        }

        System.out.println();
        System.out.println(support.getPassed() + "/" + support.getTests() + " tests reussis.");
        if (support.getPassed() != support.getTests()) {
            System.exit(1);
        }
    }
}
