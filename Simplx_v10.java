package com.ikkene.simplexe;
import java.util.ArrayList;
import java.util.Scanner;

public class Simplexe {
	public static void main(String args[]) {
		Scanner scan= new Scanner(System.in);
		int Nx,Nc=0;
		ArrayList<Double> z;
		String tempz="";
		char[] ztoarr;
		ArrayList<Double>[] sc;
		boolean res=false;
		
		StringBuilder msg = new StringBuilder();
		
		// max z ou min z
		System.out.println("Probleme de minimisation ou de maximisation?");
		System.out.println("1: max");
		System.out.println("2: min");
		
		 int num = scan.nextInt();
		 scan.nextLine();
		 
		

		
		
		//Saisie F.O
		do {
			System.out.println("Saisir la Fonction Objective");
			tempz= scan.nextLine();
			ztoarr= deleteSpaces(tempz.toCharArray());
			z=new ArrayList<Double>();
			res= traitementStringZ(ztoarr,z,false,0);
		}while(!res);
		
		Nx=z.size();
		String myString="MAX Z =";
		for(int i=0;i<z.size();i++) {
			if(i!=0 && z.get(i)>=0) myString+='+';
			myString+=" "+z.get(i)+"X"+(i+1)+" ";
		}
		System.out.println(myString);
		
		// max z ou min z
		
		 if(num == 2) {
			 for(int i=0;i<z.size();i++) {
				 z.set(i, -z.get(i));
			 }
		 }
		 // else c'est un probleme de maximisation
		 
		 
		 
		
		//Saisie Des Contraintes
		res=false;
		boolean ncinput=true;
		do {
			try {
				System.out.println("Saisir le Nombre Des Contraintes");
				Nc= scan.nextInt();
				ncinput=false;
			}catch(Exception e) {
				System.out.println(e.getMessage());
				scan.nextLine();
			}
		}while(ncinput);
		scan.nextLine();
		sc= (ArrayList<Double>[])new ArrayList[Nc];
		int actualC=0;
		for(int i=0;i<Nc;i++) {
			do {
				System.out.println("Saisir La Contrainte "+(i+1));
				tempz= scan.nextLine();
				ztoarr= deleteSpaces(tempz.toCharArray());
				sc[i]=new ArrayList<Double>();
				res= traitementStringZ(ztoarr,sc[i],true,z.size());
				if(sc[i].size()-2>Nx) {
					res = false;
				}
			}while(!res);
		}
		System.out.println("Finis Saisie Contraintes");
		int tempfordisplay=0;
		for(int i=0;i<Nc;i++) {
			myString="";
			tempfordisplay=0;
			for(int j=0;j<sc[i].size();j++) {
				if(j==sc[i].size()-2) {
					if(sc[i].get(sc[i].size()-2)==-2) {
						myString+="<";
					}
					else if(sc[i].get(sc[i].size()-2)==-1) {
						myString+="<=";
					}
					else if(sc[i].get(sc[i].size()-2)==1) {
						myString+=">=";
					}
					else if(sc[i].get(sc[i].size()-2)==2) {
						myString+=">";
					}
					else {
						myString+="=";
					}
					tempfordisplay++;;
				}
				else {
					if(j!=0 && j!=sc[i].size()-1 && sc[i].get(j)>=0) myString+="+";
					if(j!=sc[i].size()-1) myString+=sc[i].get(j)+"X"+(j+1-tempfordisplay)+" ";
					else myString+=" "+sc[i].get(j);
				}
			}
			System.out.println(myString);
		}
		
		
		
		System.out.println("\n\nFormation du tableau initial :");
		
		int l=Nc+1; // une ligne pour Z
		int c=Nc+Nx+2; // une ligne pour Z et une pour b
		double[][] mat = new double[l][c]; 
		
		remlireTabInit(mat,l,c,z,sc);
		afficherMat(mat,l,c);
		
		// base initial
		int[] base = new int[Nc];
		for(int i=Nx;i<base.length + Nx ;i++) {
			base[i-Nx] = i+1;
		}
		
		// solution initial : point extreme (0,0)
		double[] sol = new double[Nx];
		for (int i=0;i<sol.length;i++) {
			sol[i] = 0;
		}
		
		// affichage du sol init
		System.out.print("partons du point extreme : ( ");
		for(int i=0;i<sol.length;i++) {
			if(i != sol.length-1)
				System.out.print(sol[i] + " , ");
			else 
				System.out.println(sol[i] + ") ");
		}
		
		
		
		
		// algo simplexe
		boolean end = false;
		
		end = checkEnd(mat,l,c, msg);
//		if(end) System.out.println("pas de solution finie");
		int[] piv = new int [2];
		while(!end) {
			piv = getPivPos(mat,l,c);
			
			end = checkEnd(mat,l,c,msg);
			if(!end) {
				printPivot(piv,mat);
				pivoter(mat,l,c,piv);
				afficherMat(mat,l,c);
				
				
				findSolAndPrint(mat,l,c, piv,sol,base);
				for( int i=0;i<base.length;i++) {
					System.out.println();
					System.out.println(base[i]);
				}
			} 
			
		}
		
		System.out.println(msg);
	}
	private static void printPivot(int[] piv, double[][] mat) {
		System.out.print("\nle pivot est : a");
		for(int i=0;i<piv.length;i++) {
			System.out.print(piv[i]);
		}
		System.out.println(" = " +mat[piv[0]][piv[1]]);
		
	}
	private static void findSolAndPrint(double[][] mat, int l, int c, int[] piv, double[] sol, int[] base) {
		
		
		// mettre a 0 la valeur de la composante sortente de la base	
		if(base[piv[0] -1] <= sol.length) {
			sol[base[piv[0] -1]-1] = 0;
		}
		
		// changer la base 
		base[piv[0]-1] = piv[1];
		
		
		
		// mettre a jour les valeurs des composante de la base		
		for(int i=0;i<base.length;i++) {
			if(base[i]<= sol.length) {
				sol[base[i] - 1 ] = mat[i+1][c-1];

			} 
		}
		
		
		
		// print
		
		System.out.print("solution: ( ");
		for(int i=0;i<sol.length;i++) {
			if(i != sol.length-1)
				System.out.print(sol[i] + " , ");
			else 
				System.out.println(sol[i] + ") ");
		}
		
		
		
		
		
	}
	private static void pivoter(double[][] mat, int l, int c, int[] piv) {
		double[][] tempMat = new double[l][c];
		for (int i=0; i<l;i++) {
			for(int j=0;j<c;j++) {
				tempMat[i][j] = mat[i][j];
			}
		}
		// la ligne de pivot : diviser sur le pivot
		double pivot = mat[piv[0]][piv[1]];
		for(int j=1;j<c;j++) {
			mat[piv[0]][j] = tempMat[piv[0]][j]/pivot;
		}
		
		// la colone de pivot : tout est 0 sauf le pivot <= 1
		for(int i=0;i<l;i++) {
			if(i!=piv[0]) {
				mat[i][piv[1]] = 0;
			}
		}
		
		// autre : methode de rectangle 
		for(int i=0;i<l;i++) {
			if(i!=piv[0]) {
				for(int j=1;j<c;j++) {
					if(j!=piv[1])
						mat[i][j] -= (tempMat[i][piv[1]] / pivot ) * tempMat[piv[0]][j];
				}
			}
		}
		
	}
	private static int[] getPivPos(double[][] mat, int l, int c) {
		int minl = 1;
		int minc = 1;
		for(int j=1;j<c-1;j++) {
			if(mat[0][j]<mat[0][minl]) minl = j;
		}
		
		
//		double[] tabmin = new double[l-1];
//		for(int i=1;i<l;i++) {
//			if(mat[i][minl]>0) {
//				tabmin[i-1] = mat[i][c-1];
//			}else tabmin[i-1] = -1;
//		}
		
		ArrayList<Object> minarray = new ArrayList<Object>();
		for(int i=1;i<l;i++) {
			if(mat[i][minl]>0) {
				minarray.add(mat[i][c-1]/mat[i][minl]);
			}else minarray.add(null);
		}
		
		// return the pos of min in tabmin 
		int tmpmin = 0 ;
		for(int i=0;i<minarray.size();i++) {
			if(minarray.get(i) != null) {
				tmpmin = i;
				
				break;
			}
		}
		for(int i=0;i<minarray.size();i++) {
			if(minarray.get(i) != null) {
				
				if((Double)minarray.get(i) < (Double)minarray.get(tmpmin)) {
					tmpmin = i;
				}
			}
				
		}
		minc = tmpmin + 1;
		
		return new int[]{minc,minl};
	}
	private static boolean checkEnd(double[][] mat, int l, int c,StringBuilder msg) {
		// pas de solution finie
		int countNegZ = 0;
		int neg;
		
		for(int j=1;j<c-1;j++) {
			neg = 0;
			if(mat[0][j]<0){
				
				countNegZ++;
				for(int i=1;i<l;i++) {
					if(mat[i][j]<=0) {
						neg++;
					}
				}
				if(neg == l-1) {
					msg.append("pas de solution finie");
					return true;
				};
			}
			
		}
		
		
		if(countNegZ == 0) {
			msg.append("solution atteinte");
			return true;
		};
		
		
		return false;
		
	}
	private static void afficherMat(double[][] mat, int l, int c) {
		for(int i=0;i<l;i++) {
			if(i==1) System.out.println("------------------------------------------------");
			for(int j=0;j<c;j++) {
				if(j==0 || j== c-2)System.out.print(mat[i][j] + " | ");
				else System.out.print(mat[i][j] + "	");
			}
			System.out.println();
		}
		
	}
	private static void remlireTabInit(double[][] mat, int l, int c, ArrayList<Double> z, ArrayList<Double>[] sc) {
		//mat[0][0]=1;
		//remplire la colonne de Z
		for(int i=0;i<l;i++) {
			if(i==0) mat[i][0]=1;
			else mat[i][0]=0;
		}
		//remplire la ligne de Z
		for(int j=1;j<c;j++) {
			if(j<=z.size()) mat[0][j] = -z.get(j-1);
			else mat[0][j]=0;
		}
		//remplire la matrice N
		for(int i=1;i<l;i++) {
			for(int j=1;j<=z.size();j++) {
				mat[i][j]=sc[i-1].get(j-1);
			}
		}
		//remplire la matrice I
		for(int i=1;i<l;i++) {
			for(int j=z.size()+1;j<c-1;j++) {
				if(i==j-z.size()) {
					mat[i][j]=1;
				}else {
					mat[i][j]=0;
				}
			}
		}
		//remplire la colonne de b
		for(int i=1;i<l;i++) {
			mat[i][c-1] = sc[i-1].get(sc[i-1].size()-1);
			
		}
		
		
		
		
	}
	static boolean traitementStringZ(char[] ztoarr,ArrayList<Double> z,boolean b,int zsize) {
		if(ztoarr.length<3) return false;
		String floatt;
		int t=0;
		boolean noSym = true;
		for(int i=0;i<ztoarr.length;i++) {
			
			floatt="";
			int k=0;
			boolean readLastAndQuit=false;
			if(i!=0 && b==true) {
				
				try {
					//System.out.println("//"+ztoarr[i]+"//");
					switch(ztoarr[i]) {
					case '<':
						noSym = false;
						for(int j=z.size();j<zsize;j++) z.add(0.0);
						if(ztoarr[i+1]=='=') {
							z.add(-1.0);
							k=i+2;
						}
						else {
							z.add(-2.0);
							k=i+1;
						}
						readLastAndQuit=true;
						break;
					case '>':
						noSym = false;
						for(int j=z.size();j<zsize;j++) z.add(0.0);
						if(ztoarr[i+1]=='=') {
							z.add(1.0);
							k=i+2;
						}
						else {
							z.add(2.0);
							k=i+1;
						}
						readLastAndQuit=true;
						break;
					case '=':
						noSym = false;
						for(int j=z.size();j<zsize;j++) z.add(0.0);
						z.add(0.0);
						k=i+1;
						readLastAndQuit=true;
						break;
					default:
						//System.out.println("Default");
					}
				}catch(Exception e) {
					return false;
				}
				if(readLastAndQuit) {
					for(int j=k;j<ztoarr.length;j++) {
						floatt+=ztoarr[j];
					}
					//System.out.println("||"+floatt+"||");
					try {
						z.add(Double.parseDouble(floatt));
					}catch(Exception e) {
						System.out.print(e.getMessage());
						return false;
					}
					return true;
				}
			}
			for(int j=i;j<ztoarr.length && ztoarr[j]!='x';j++) {
				//System.out.println(ztoarr[j]);
				floatt+=ztoarr[j];
			}
			t=0;
			if(floatt.equals("") || floatt.equals("+")) {
				t=1;
			}
			if(floatt.equals("-")) {
				t=-1;
			}
			try {
				//System.out.println(floatt);
				//System.out.println(floatt);
				//System.out.println("ghhhjh"+ztoarr[i+floatt.length()+1-t]);
				if(!Character.isDigit(ztoarr[i+floatt.length()+1])) {
					//System.out.println("ghhhjh"+ztoarr[i+floatt.length()+1]);
					return false;
				}
				if(b) for(int j=z.size();j<Integer.parseInt(ztoarr[i+floatt.length()+1]+"")-1;j++) z.add(0.0);
				if(t==1) z.add(1.0);
				else if (t==-1) z.add(-1.0);
				else z.add(Double.parseDouble(floatt));
				i+=floatt.length()+1;
			}catch(Exception e) {
				System.out.print(e.getMessage());
				return false;
			}
		}
		if(b && noSym) {
			System.out.println("here");
			return false;
		}
		return true;
	}
	static char[] deleteSpaces(char[] arr1) {
		String temp="";
		for(int i=0;i<arr1.length;i++) {
			if(arr1[i]!=' ') temp+=arr1[i];
		}
		return temp.toCharArray();
	}
}
