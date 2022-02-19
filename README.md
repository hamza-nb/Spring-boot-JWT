**Objectifs**

L&#39;objectif du mini-projet est de développer les apis suivantes:

1. Génération d&#39;utilisateurs

method: GET

url: /api/users/generate

content-type: application/json

secured: no

parameters:

- count: number

Cet premier endpoint REST permet de générer un fichier json contenant count éléments en respectant la structure suivante:

[{

  &quot;firstName&quot;: &quot;string&quot;,

 &quot;lastName&quot;: &quot;string&quot;,

 &quot;birthDate&quot;: &quot;date&quot;,

 &quot;city&quot;: &quot;ville&quot;,

 &quot;country&quot;: &quot;code iso2&quot;,

 &quot;avatar&quot;: &quot;url d&#39;une image&quot;,

 &quot;company&quot;: &quot;string&quot;,

 &quot;jobPosition&quot;: &quot;string&quot;,

 &quot;mobile&quot;: &quot;numéro de téléphone&quot;,

 &quot;username&quot;: &quot;identifiant de connexion&quot;,

 &quot;email&quot;: &quot;adresse email&quot;,

 &quot;password&quot;: &quot;mot de passe alétoire entre 6 et 10 caractères&quot;,

 &quot;role&quot;: &quot;admin ou user&quot;

}]

Tous les champs de ce fichier JSON doivent être générés de façon à avoir des résultants vraisemblables en utilisant une librairie Java appropriée. Les contenus tels que &quot;example, test, etc&quot; ne sont pas recevables.

Le champs role doit etre généré en choisissant parmi les deux valeurs role ou admin

En mettant l&#39;URL de cette API dans le navigateur (ex: [http://localhost:9090/api/users/generate?count=100](http://localhost:9090/api/users/generate?count=100) le téléchargement d&#39;un fichier JSON doit être déclenché. Le JSON ne doit pas être affiché sous forme texte dans le navigateur web.

1. Upload du fichier utilisateurs et création des utilisateurs en base de données

method: POST

url: /api/users/batch

content-type: multipart/form-data

secured: no

parameters:

- file: multipart-file

Ce second endpoint permet d&#39;uploader le fichier JSON généré en #1. Le fichier doit être importé en base de données en vérifiant les doublons sur l&#39;adresse email et le username (uniques). Une réponse JSON doit être retournée pour résumer le nombre d&#39;enregistrements total, importés avec succès et non importés. Avant l&#39;enregistrement en base de données, le mot de passe doit être encodé et non stocké en clair.

1. Connexion utilisateur + génération JWT

method: POST

url: /api/auth

content-type: application/json

request-body:

- username: string

- password: string

Ce 3e endpoint permet d&#39;authentifier un utilisateur et de générer un token JWT valide. La réponse est un JSON respectant la structure suivante:

{ &quot;accessToken&quot;: &quot;valeur du jeton JWT&quot; }

Le JWT généré doit contenir l&#39;email de l&#39;utilisateur. Pour s&#39;authentifier, il est possible de renseigner dans le champs username soit le username soit l&#39;email contenu dans le fichier JSON importé.

1. Consultation de mon profil

method: GET

url: /api/users/me

secured: yes

Ce 4e endpoint permet d&#39;utiliser le jeton JWT pour accéder de façon sécurisée au profil de l&#39;utilisateur associé.

1. Consultation de mon profil

method: GET

url: /api/users/{username}

sécurisée: oui

Lorsque le JWT utilisé correspond à un utilisateur ayant le rôle admin, il est possible de consulter le profil de n&#39;importe quel autre utilisateur. Un utilisateur n&#39;ayant pas le rôle admin ne peut donc pas accéder au profil d&#39;un autre utilisateur
