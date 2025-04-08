public class Card {
	public final int cardId;

	public Card(int cardId) {
		this.cardId = cardId;
	}

	public int getRank() {
		return (cardId - 1) % 13 + 1;
	}

	public String getSuit() {
		switch ((cardId - 1) / 13) {
			case 0: return "Spades";
			case 1: return "Hearts";
			case 2: return "Diamonds";
			case 3: return "Clubs";
			default: return "Unknown";
		}
	}

	public int getScore() {
		int rank = getRank();
		if (rank == 1) return 11;
		if (rank >= 11 && rank <= 13) return 10;
		return rank;
	}
}
