# Brain

Brain is a facade for the OWL-API, wrapping the Elk reasoner for
 reasoning task, such as query or consistency checking.

Brain has been designed with biomedical knowledge in mind, which can 
expressed as ontology or knowledge-base using the Web Ontology Language (OWL). 
Biomedical knowledge-bases are usually very large and mostly focused on the 
TBox (lots of classes, few instances). Biomedical ontologies are therefore very often following
 an OWL 2 EL profile, suitable for fast reasoning (decidable in polynomial time).

The initial goal of Brain is to 
provide a simplified interaction with the OWL-API to analyse 
biological data-sets and manipulate ontologies. Brain aims also at bridging the gap 
between Description Logic and Biology.

# Documentation

Read the wiki for more details: https://github.com/loopasam/Brain/wiki