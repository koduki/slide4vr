```bash
docker run -it --rm -e PORT=5000 -p 5000:5000 -v /mnt/c/Users/koduki/Downloads/:/secret -e GOOGLE_APPLICATION_CREDENTIALS=/secret/slide2vr-1682c525dd5c.json koduki/pptx2png
curl -iv -X GET "http://localhost:5000/?args=itnews/202008"
```

```bash
gcloud builds submit
```
