# Sample Presigned URL Generator for Cloudscale

I run this sample program using a Ceph Docker image (v2.10.7) and it worked just fine but it fails currently on
Cloudscale. I suspect the proxy might be changing some headers thereby invalidating the signature but I may be wrong.

## Usage

1. Ensure you have Java and Maven installed (`apt install java8-sdk maven` or similar)
2. adjust `ACCESS_KEY`, etc. in [`Main.java`](src/main/java/ch/arbitrary/cloudscale/sample1/Main.java)
2. `mvn install`
3. `java -jar target/sample1-1.0-SNAPSHOT.jar`

This is the output I get:

```
creating object 011539b6-f347-48e0-975a-3cbd52c68095
ok
getting object
ok
generating presigned URL
ok, presigned URL is: https://sample1.objects.cloudscale.ch/011539b6-f347-48e0-975a-3cbd52c68095?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20170523T142357Z&X-Amz-SignedHeaders=host&X-Amz-Expires=899&X-Amz-Credential=YIAD0DQPNBV5R1EFCJOX%2F20170523%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=e4e2fdcca1dd2d537e3b311e18285eab36e5307fd4c78ca49c5abf6c7c9c10e9
requesting presigned URL
Exception in thread "main" java.lang.AssertionError: unexpected content: <?xml version="1.0" encoding="UTF-8"?><Error><Code>SignatureDoesNotMatch</Code><RequestId>tx000000000000000417d38-00592445fe-6e01199-default</RequestId><HostId>6e01199-default-default</HostId></Error>
        at ch.arbitrary.cloudscale.sample1.Main.main(Main.java:72)
```

Creating and getting the objects works, but fetching the presigned URL fails with SignatureDoesNotMatch.


## Docker Command Used

I set up the local docker image like this:

1. Replace parameters in [`Main.java`](src/main/java/ch/arbitrary/cloudscale/sample1/Main.java):

    ```java
    private static final String ACCESS_KEY = "access_key";
    private static final String SECRET_KEY = "secret_key";
    private static final String BUCKET_NAME = "sample1";
    private static final String ENDPOINT = "http://localhost:8088";
    ```

2 . `docker run --env CEPH_DEMO_ACCESS_KEY=access_key --env CEPH_DEMO_SECRET_KEY=secret_key --env CEPH_DEMO_UID=demo_uid  --env MON_IP=127.0.0.1 --env PH_PUBLIC_NETWORK=0.0.0.0/0 -p 8088:80 ceph/demo:tag-build-master-jewel-ubuntu-16.04`

3 . create `sample1` bucket

