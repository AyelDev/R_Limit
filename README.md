as a beginner of this matter what should i learn later on
Where you are now

✅ Basic rate limiting concept
✅ Fixed window implementation
✅ Servlet filters in Spring Boot


Next steps (in order)
1. Understand the other algorithms

Implement sliding window yourself
Implement token bucket yourself
Don't just copy — build it from scratch so you understand the tradeoffs

2. Storage

Right now counts live in memory — they reset when the server restarts
Learn Redis for storing rate limit counters
This also lets rate limiting work across multiple servers (distributed systems)

3. Spring Security

Rate limiting often works alongside authentication
Limit by user ID or API key instead of just IP
IP-based limiting is easy to bypass with VPNs

4. Real libraries

Bucket4j — most popular Java rate limiting library
Resilience4j — broader fault tolerance (rate limit + retry + circuit breaker)
Learn these after building your own so you appreciate what they solve

5. Deeper concepts

Circuit breaker pattern
Retry with exponential backoff + jitter (we touched on this)
Throttling vs rate limiting difference


Recommended order of projects
Your current project (Fixed window)
        ↓
Upgrade to sliding window
        ↓
Add Redis storage
        ↓
Add API key based limiting
        ↓
Rewrite using Bucket4j
The best way to learn this is to break your own implementation first — try to bypass it, then fix it.

reference link:
https://www.geeksforgeeks.org/advance-java/implementing-rate-limiting-in-a-spring-boot-application/