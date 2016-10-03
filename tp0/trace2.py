#!/usr/bin/env python
# -*- coding: utf-8 -*-
# Abonnenc, Bonhoure
# septembre 2016

import sys
from math import *

def resize(x,xmin,xmax) :
	res = x - (0.5 * xmin + 0.5* xmax); #courbe centrée sur l'axe
	res = res * (20.0 / (xmax - xmin)); # adapter à la taille de l'interval
	return res;


def val_axes(xmin,xmax,ymin,ymax,output) :
	for i in range(5) :
		output.write(" -10.2 cm %d cm moveto\n (%4.2f)\n" % (10 - (5*i),ymax -(i*(ymax-ymin)/4.0)));
		#Coller le texte à droite
		output.write("""
dup stringwidth pop
0 exch sub
0 rmoveto
show
	""")
		output.write(" %d cm -10.8 cm moveto\n (%4.2f)\n" % (10 - (5*i),xmax -(i*(xmax-xmin)/4.0)));
		#Centrer le texte
		output.write("""
dup stringwidth pop
2 div
0 exch sub
0 rmoveto
show
	""")
		
# Affiche le repère, avec les valeurs des axes ainsi que la fonction en dessous.
def repere(xmin,xmax,ymin,ymax,function,output) :
	# dessin du cadre
	output.write("""
/repere {
    /Arial findfont
    .5 cm scalefont
    setfont
    
    newpath
    
    -10 cm 10 cm moveto
    10 cm 10 cm lineto
    10 cm -10 cm lineto
    -10 cm -10 cm lineto
    -10 cm 10 cm lineto
    
    -10 cm 10.5 cm moveto
    (y) show
    
    10.5 cm -10 cm moveto 
    (x) show
	""")
	# Affichage des valeurs des axes
	val_axes(xmin,xmax,ymin,ymax,output);
	
	# dessin de l'axe du milieu
	output.write("""
    -10 cm 0 cm moveto
    10 cm 0 cm lineto
    0 cm -10 cm moveto
    0 cm 10 cm lineto
    
    stroke
    """)
    #dessin des sous axes
	output.write("""
	newpath
	
	[3 3] 0 setdash
    -10 cm 5 cm moveto
    10 cm 5 cm lineto
    -10 cm -5 cm moveto
    10 cm -5 cm lineto
    
    -5 cm 10 cm moveto
    -5 cm -10 cm lineto
	5 cm 10 cm moveto
    5 cm -10 cm lineto
    
    stroke
    """)
    # fin de la fonction
	output.write("""
} def
repere
	""")

def ecrire_nom_fonc(output,nomfunction) :
	output.write("""%!PS-Adobe-3.0
0 cm 11 cm moveto
/Arial findfont 18 scalefont setfont
""")
	output.write("(Fonction : %s)" % (nomfunction))
	output.write("""
dup stringwidth pop
2 div
0 exch sub
0 rmoveto
show
	""")	
	
def trace(function, xmin, xmax, nstep, output):
	
	output.write("%!\n/cm { 25 mul } def\n")
	output.write("12 cm 14.85 cm translate") #Centrer le repere sur la feuille
	nomfunction = function
	function = eval("lambda x:" + function)
	
	ymin = function(xmin)
	ymax = ymin
	
	# calcul du ymin et ymax
	step = 1.*(xmax-xmin)/nstep
	for i in range(nstep+1):
		x = xmin + i*step
		try:
			y = function(x)
		except:
			continue
		if y > ymax : ymax = y
		if y < ymin : ymin = y
	
	# Repere 
	repere(xmin,xmax,ymin,ymax,function,output)		
	# Fonction en dessous du graphe
	ecrire_nom_fonc(output,nomfunction)
	
	# xmin doit être en -10cm et xmax en 10cm
	# commencer la courbe en elle même en metant l'épaisseur à 2 et la ligne continue
	output.write("newpath\n 2 setlinewidth [1 0] 0 setdash\n%s cm %s cm moveto\n" %(resize(xmin,xmin,xmax),resize(function(xmin),ymin,ymax)));
	
	step = 1.*(xmax-xmin)/nstep
	for i in range(nstep+1):
		x = xmin + i*step
		try:
			y = function(x)
		except:
			continue
		output.write("%s cm %s cm lineto\n" % (resize(x,xmin,xmax), resize(y,ymin,ymax)));
		
	output.write("stroke\n")	
	output.write("showpage\n")
		

def main(argv=None):
	"""	
	Renvoie les valeurs de la fonction (en x) passée en paramètre entre les bornes xmin et xmax avec nstep valeurs sous forme de fichier postscript
	
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
		
	# Inverser les bornes mine t max si nécessaire
	if(xmin > xmax) :
		a = xmin
		xmin = xmax
		xmax = a
		
	trace(function, xmin, xmax, nstep, output)


if __name__ == "__main__":
	sys.exit(main())

