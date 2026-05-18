/**
 * Contrat minimal pour une suite de tests du projet.
 */
public interface TestSuite {
    /**
     * Execute les tests de la suite.
     *
     * @param support utilitaires d'assertion et de preparation de plateau
     */
    void run(TestSupport support);
}
