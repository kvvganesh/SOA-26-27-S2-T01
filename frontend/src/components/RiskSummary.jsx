function RiskSummary({ loginResponse }) {

  const riskLevel =
    loginResponse?.riskLevel || "N/A";

  const ruleScore =
    loginResponse?.ruleScore ?? "N/A";

  const aiRisk =
    loginResponse?.aiRisk || "N/A";

  const aiProbabilities =
    loginResponse?.aiProbabilities || null;

  return (
    <section>
      <h2>Risk Assessment</h2>

      <p>
        Risk Level:{" "}
        <strong>{riskLevel}</strong>
      </p>

      <p>
        Rule Score:{" "}
        <strong>{ruleScore}</strong>
      </p>

      <p>
        AI Risk:{" "}
        <strong>{aiRisk}</strong>
      </p>

      {aiProbabilities && (
        <div>
          <h3>AI Risk Probabilities</h3>

          {Object.entries(aiProbabilities).map(
            ([risk, probability]) => (
              <p key={risk}>
                {risk}:{" "}
                {(probability * 100).toFixed(2)}%
              </p>
            )
          )}
        </div>
      )}
    </section>
  );
}

export default RiskSummary;