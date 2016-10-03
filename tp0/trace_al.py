#!/usr/bin/env python
# -*- coding: utf-8 -*-
# Abonnenc, Bonhoure
# septembre 2016

import sys
from math import *


def trace(function, xmin, xmax, nstep, output):
	output.write("Fonction : x --> %s\n" % function)
	output.write("Bornes : xmin = %d  xmax = %d\nPas : nstep = %d\n" % (xmin,xmax,nstep))
	function = eval("lambda x:" + function)
	
	step = 1.*(xmax-xmin)/nstep
	for i in range(nstep+1):
		x = xmin + i*step
		try:
			y = function(x)
		except:
			continue
		output.write("%s, %s\n" % (x, y))
		

def main(argv=None):
	"""	
	Renvoie les valeurs de la fonction (en x) passée en paramètre entre les bornes xmin et xmax avec nstep valeurs
	
	Comment lancer le programme
		./trace.py "nom_de_fonction_à_tester" [xmin] [xmax] [nstep]
	Exemple
		./trace.py "sin(x)" 0 1 20
		./trace.py -o fichier "cos(x)"
		./trace.py "x+5*x*x" 2 3
		
	Attention : Les options doivent être placée avant les arguments.
	
	Si vous ne précisez pas de valeurs pour les bornes ou le nombre de valeurs
	xmin = 0
	xmax = 1
	nstep = 10
	
	Si vous donnez une borne xmin > à xmax, les valeurs seront données dans l'orde décroissant
	
	Options
	-o, --output
		écrit la sortie de l'exécution de ce programme dans le fichier dont le nom suit l'option.
	
	-h, --help
		affiche ce manuel
	"""
	if argv is None:
		argv = sys.argv
	
	import getopt
	try:
		options, argv = getopt.getopt(argv[1:], "o:h", ["output=","help"])
	except getopt.GetoptError as message:
		sys.stderr.write("%s\n" % message)
		sys.exit(1)
		
	output = sys.stdout
	xmin, xmax = 0., 1.
	nstep = 10
	
	for option, value in options:
		if option in ["-o", "--output"]:
			output = file(value, "w")
		
		elif option in ["-h", "--help"]: # Option ajoutée pour pouvoir appeler la documentation à l'aide d'une option lors de l'appel de la fontion
			help(main)
			sys.exit(2)
		else:
			sys.stderr.write("option inconnue\n")
			assert False, "unhandled option"
			
	nbargs = len(argv)
	if nbargs < 1:
		sys.stderr.write("//Erreur// Veuillez entrer des arguments.\nSi vous avez besoin d'aide entrez l'option -h ou --help\n")
		sys.exit(1)
	else :
		function = argv[0]
		if nbargs >= 2 :
			xmin = int(argv[1])
			if nbargs >= 3 :
				xmax = int(argv[2])
				if nbargs == 4 :
					nstep = int(argv[3])
				elif nbargs > 4 :
					sys.stderr.write("//Erreur// Veuillez entrer au plus 4 arguments.\nSi vous avez besoin d'aide entrez l'option -h ou --help\n")
					sys.exit(3)
			
	trace(function, xmin, xmax, nstep, output)


if __name__ == "__main__":
	sys.exit(main())
