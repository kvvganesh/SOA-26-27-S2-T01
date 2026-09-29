function SecuritySuggestions({
  suggestions
}) {

  return (
    <section>

      <h2>Security Suggestions</h2>

      {suggestions?.length > 0 ? (

        suggestions.map(
          (suggestion, index) => (

            <div key={index}>

              <h3>
                {suggestion.type}
              </h3>

              <p>
                {suggestion.message}
              </p>

            </div>
          )
        )

      ) : (

        <p>
          No additional security actions
          are required.
        </p>

      )}

    </section>
  );
}

export default SecuritySuggestions;