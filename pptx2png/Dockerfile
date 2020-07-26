FROM debian

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        libreoffice \
        fonts-noto-cjk-extra fonts-liberation2 \
        imagemagick \
        poppler-utils \
        xpdf \
        curl \
        ca-certificates \
        && \ 
    apt-get -y --purge autoremove && \
    rm -rf /var/lib/apt/lists/*

RUN echo "deb [signed-by=/usr/share/keyrings/cloud.google.gpg] http://packages.cloud.google.com/apt cloud-sdk main" | tee -a /etc/apt/sources.list.d/google-cloud-sdk.list && \
    curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key --keyring /usr/share/keyrings/cloud.google.gpg add - && \
    apt-get update && apt-get install -y google-cloud-sdk && \
    apt-get -y --purge autoremove &&  rm -rf /var/lib/apt/lists/*

ADD sh/pptx2png.sh /usr/bin/pptx2png
ADD sh/run.sh /usr/bin/run.sh
RUN curl https://storage.googleapis.com/shared-artifact/hwrap -o /usr/bin/hwrap && chmod a+x /usr/bin/hwrap 

RUN mkdir -p /home/slide2vr && useradd --home-dir /home/slide2vr slide2vr && chown -R slide2vr:slide2vr /home/slide2vr
USER slide2vr
WORKDIR /home/slide2vr

ENV PORT=5000
CMD /usr/bin/hwrap -p $PORT /usr/bin/run.sh