import http from "k6/http";
import { check, sleep } from "k6";

const BASE_URL = __ENV.BASE_URL || "http://localhost:18080";
const AUTH_HEADERS = { Authorization: "Basic YWRtaW46YWRtaW4xMjM=" };

export const options = {
    stages: [
        { duration: "10s", target: 5 },
        { duration: "20s", target: 5 },
        { duration: "10s", target: 0 }
    ],
    thresholds: {
        http_req_failed: ["rate<0.01"],
        http_req_duration: ["p(95)<1000"],
        checks: ["rate>0.99"]
    }
};

const endpoints = [
    "/api/statistika",
    "/api/ucenici",
    "/api/profesori",
    "/api/kursevi",
    "/api/upisi",
    "/api/termini"
];

export default function () {
  const responses = http.batch(endpoints.map(path => ["GET", `${BASE_URL}${path}`, null, { headers: AUTH_HEADERS }]));

    responses.forEach((response, index) => {
        check(response, {
            [`${endpoints[index]} vraca status 200`]: result => result.status === 200,
            [`${endpoints[index]} vraca JSON`]: result =>
                (result.headers["Content-Type"] || "").includes("application/json")
        });
    });

    sleep(1);
}

export function handleSummary(data) {
    return {
        "target/k6-summary.json": JSON.stringify(data, null, 2)
    };
}
