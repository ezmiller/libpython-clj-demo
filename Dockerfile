FROM simplect/clojupyter:0.2.2

EXPOSE 8888
EXPOSE 4444

# For building locally select your favorite mirror
ENV REPO "http://fr.archive.ubuntu.com/ubuntu bionic main"

USER root

RUN echo "deb http://fr.archive.ubuntu.com/ubuntu bionic main" >> /etc/apt/sources.list &&\
    apt-get update &&\
    apt-get install libpython3.6-dev python3-pip -y --allow-unauthenticated &&\
    pip3 install numpy pandas jinja2 xlrd sklearn gensim

USER jovyan

# CMD ["jupyter", "notebook", "--log-level=ERROR", "--allow-root"]
# CMD ["lein", "repl", ":headless", ":host", "0.0.0.0", ":port", "4444"]

CMD lein update-in :dependencies conj "[nrepl \"0.6.0\"]" -- update-in :plugins conj "[refactor-nrepl \"2.4.0\"]" -- update-in :plugins conj "[cider/cider-nrepl \"0.22.0-beta2\"]" -- update-in :plugins conj "[nubank/midje-nrepl \"1.2.0-SNAPSHOT\"]" -- repl :headless :host 0.0.0.0 :port 4444


