
public class ThaiConsonantFlashcard extends Flashcard {

    private final String symbol;          // ก
    private final String thaiReading;     // กอ ไก่
    private final String romanized;       // gor gai
    private final String toneClass;       // Mid

    public ThaiConsonantFlashcard(String symbol, String thaiReading,
            String romanized, String toneClass) {
        // For Flashcard superclass, set front = symbol + thaiReading
        // and back = romanized + toneClass
        super(symbol + "  " + thaiReading,
                romanized + "  (Tone: " + toneClass + ")");

        this.symbol = symbol;
        this.thaiReading = thaiReading;
        this.romanized = romanized;
        this.toneClass = toneClass;
    }

    // Getters
    public String getSymbol() {
        return symbol;
    }

    public String getThaiReading() {
        return thaiReading;
    }

    public String getRomanized() {
        return romanized;
    }

    public String getToneClass() {
        return toneClass;
    }

    // Optional: pretty display
    @Override
    public String toString() {
        return "ThaiConsonantFlashcard {"
                + "Symbol='" + symbol + '\''
                + ", Thai Reading='" + thaiReading + '\''
                + ", Romanized='" + romanized + '\''
                + ", Tone Class='" + toneClass + '\''
                + '}';
    }

}
