FROM debian

RUN apt-get update && apt-get upgrade -y

RUN apt-get install -y nginx

RUN rm /etc/nginx/sites-enabled/default

CMD ["nginx", "-g","daemon off;"]




