import Icon from "./Icons";

function SecuritySuggestions({
  suggestions
}) {

  return (
    <section className="suggestions">

      {suggestions?.length > 0 ? (

        suggestions.map(
          (suggestion, index) => (

            <div className="suggestion" key={index}>

              <span className="suggestion-icon">
                <Icon name="alert" size={18} />
              </span>

              <div>
                <h3>
                  {suggestion.type}
                </h3>

                <p>
                  {suggestion.message}
                </p>
              </div>

            </div>
          )
        )

      ) : (

        <div className="suggestion ok">
          <span className="suggestion-icon">
            <Icon name="check" size={18} />
          </span>
          <div>
            <h3>You're all set</h3>
            <p>No additional security actions are required.</p>
          </div>
        </div>

      )}

    </section>
  );
}

export default SecuritySuggestions;
