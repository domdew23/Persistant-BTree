
public class Team {

	int index;
	String team;
	double dvoa;
	int lastweek;
	double weightedDef;
	int weightedRank;
	double passDEF;
	int passRank;
	double rushDEF;
	int rushRank;
	double nonAdjTotal;
	double nonAdjPass;
	double nonAdjRush;
	double var;
	int varRank;
	double sched;
	int schedRank;

	public Team(int index, String team, double dvoa, int lastweek, double weightedDef, int weightedRank, double passDEF,
			int passRank, double rushDEF, int rushRank, double nonAdjTotal, double nonAdjPass, double nonAdjRush,
			double var, int varRank, double sched, int schedRank) {

		this.index = index;
		this.team = team;
		this.dvoa = dvoa;
		this.lastweek = lastweek;
		this.weightedDef = weightedDef;
		this.weightedRank = weightedRank;
		this.passDEF = passDEF;
		this.passRank = passRank;
		this.rushDEF = rushDEF;
		this.rushRank = rushRank;
		this.nonAdjTotal = nonAdjTotal;
		this.nonAdjPass = nonAdjPass;
		this.nonAdjRush = nonAdjRush;
		this.var = var;
		this.varRank = varRank;
		this.sched = sched;
		this.schedRank = schedRank;
	}

	public double getAvgRank() {
		double total = passRank + rushRank;
		// System.out.println("TEAM: " + team + " PASS " + passRank + " RUSH " +
		// rushRank);
		return total / 2;
	}

	public String toString() {
		return " Team: " + team + "\n DVOA: " + dvoa + "\n weightedDef: " + weightedDef + "\n weightedRank: "
				+ weightedRank + "\n passDEF " + passDEF + "\n passRank: " + passRank + "\n rushDEF " + rushRank
				+ "\n nonadjTotal " + nonAdjTotal + "\n nonAdjPass " + nonAdjPass + "\n nonAdjRush " + nonAdjRush
				+ "\n var " + var + "\n varRank " + varRank + "\n sched " + sched + "\n schedRank " + schedRank;
	}

}
