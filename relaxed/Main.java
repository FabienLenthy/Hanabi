
public class Main{

	public static void main(String [] args){
		
		Tests tests = new Tests();
		String error = "WRONG INPUT! You should type:\n"
				+ "- \"game\" to simulate a game\n"
				+ "- \"average n\" to get the average score over n simulations\n"
				+ "- \"demo\" to simulate a game on a given deck";
		if(args.length < 1) {
			System.out.println(error);
		}
		else if(args[0].equals("game")) {
			tests.printGame();
		}
		else if(args[0].equals("average")){
			if(args.length < 2) {
				System.out.println(error);
			}
			else {
				System.out.println(tests.averageScore(Integer.parseInt(args[1])));
			}
		}
		else if(args[0].equals("decision")) {
			tests.printDecisionMaking();
		}
		else if(args[0].equals("demo")) {
			tests.demo();
		}
		else {
			System.out.println(error);
		}
	}
}
