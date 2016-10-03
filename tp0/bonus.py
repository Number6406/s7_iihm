#!/usr/bin/env python
# -*- coding: utf-8 -*-
# Abonnenc, Bonhoure
# septembre 2016

import sys
from math import *


def do(n,output,size) :
	if(n == 0) :
		output.write(" %s cm 0 cm lineto\n" % size)
		output.write(" %s cm 0 cm translate\n" % size)

		
	else :
		# Là c chô frère
		
		do(n-1,output,size/3.0)
		
		output.write(" 60 rotate\n")
		do(n-1,output,size/3.0)
		
		output.write(" -120 rotate\n")
		do(n-1,output,size/3.0)
		
		output.write(" 60 rotate\n")
		do(n-1,output,size/3.0)
	
def trace(n, output):
	
	output.write("%!\n/cm { 27 mul } def\n")
	xmilieu = 1
	ymilieu = 14.85
	output.write("%s cm %s cm translate\n" % (xmilieu,ymilieu))
	# xmin doit être en -10cm et xmax en 10cm
	
	output.write("newpath\n %s cm %s cm moveto\n" %(0,0));
	
	do(n,output,20.0)
		
	output.write("stroke\n")	
	output.write("showpage\n")
		

def main(argv=None):
	
	if argv is None:
		argv = sys.argv
	
	output = sys.stdout
	n = 1		
	
	nbargs = len(argv)
	if nbargs < 2:
		sys.stderr.write("""//Erreur// Veuillez entrer un entier > 0 pour le nombre d'itération
Vous pouvez entrer en deuxième argument le nom du fichier de sortie.
Sinon la sortie postscript sera affichée à l'écran.

Comment lancer le programme
	./bonus.py N [fich]
	avec N le nombre d'itération et fich le nom de fichier de sortie (optionnel)
Exemple
	./trace.py 5 koch.ps
""")
		sys.exit(1)
	else :
		n = int(argv[1])
		if nbargs >= 3 :
			output = file(argv[2],"w")
		
	if(n == 0) : sys.exit(2)
	elif( n < 0 ) : n = -n
		
	trace(n, output)


if __name__ == "__main__":
	sys.exit(main())

