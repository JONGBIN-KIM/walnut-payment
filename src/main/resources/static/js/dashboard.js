document.addEventListener("DOMContentLoaded", function () {
    // 이전에 로드된 로그를 저장하는 변수
    const logsCache = {
        pg: new Set(),
        token: new Set(),
        van: new Set(),
    };
    const systemUrls = {
        pg: "http://localhost:8443/api/logs?file=pg-system.log",
        token: "http://localhost:8445/api/logs?file=token-system.log",
        van: "http://localhost:8444/api/logs?file=van-system.log",
    };

    const addCardToTable = (card) => {
        const cardTableBody = document.getElementById("cardTable").querySelector("tbody");
        const row = `<tr>
            <td>${card.cardIdentifier}</td>
            <td>${card.ci}</td>
            <td>${card.refId}</td>
            <td>${new Date().toLocaleString()}</td>
        </tr>`;
        cardTableBody.innerHTML += row;
    };

    const fetchCardDetails = (cardIdentifier) => {
        return fetch(`http://localhost:8443/api/cards/${cardIdentifier}`)
            .then((response) => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            });
    };

    const fetchAndDisplayCards = () => {
        fetch("http://localhost:8443/api/cards")
            .then(response => response.json())
            .then(data => {
                const cardTableBody = document.getElementById("cardTable").querySelector("tbody");
                cardTableBody.innerHTML = ""; // Clear existing rows
                data.forEach(card => {
                    const row = `<tr>
                        <td>${card.ci}</td>
                        <td>${card.refId}</td>
                        <td>${card.createdAt}</td>
                    </tr>`;
                    cardTableBody.innerHTML += row;
                });
            })
            .catch(error => console.error("Failed to fetch card details:", error));
    };

    // Call fetchAndDisplayCards on load
    fetchAndDisplayCards();

    function fetchAndDisplayTransactions() {
        fetch("http://localhost:8443/api/transactions") // 거래 내역 API 호출
            .then((response) => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then((data) => {
                console.log("Transaction data fetched successfully:", data);

                // 거래 내역 표 갱신
                const transactionTable = document.getElementById("transactionTable").getElementsByTagName("tbody")[0];
                transactionTable.innerHTML = ""; // 기존 데이터를 초기화

                data.forEach((transaction) => {
                    const row = transactionTable.insertRow();

                    const transactionIdCell = row.insertCell(0);
                    transactionIdCell.textContent = transaction.transactionId;

                    const amountCell = row.insertCell(1);
                    amountCell.textContent = `${transaction.amount} ${transaction.currency}`;

                    const statusCell = row.insertCell(2);
                    statusCell.textContent = transaction.status;

                    const dateCell = row.insertCell(3);
                    dateCell.textContent = transaction.approvedAt ? new Date(transaction.approvedAt).toLocaleString() : "N/A";
                });
            })
            .catch((error) => {
                console.error("Error fetching transaction data:", error);
                alert("Failed to fetch transaction data. Please try again.");
            });
    }


    // 카드 등록
    document.getElementById("registerCardButton").addEventListener("click", function () {
        const requestUniqueId = document.getElementById("requestUniqueId").value;
        const ci = document.getElementById("ci").value;
        const encryptedCardInfo = document.getElementById("encryptedCardInfo").value;
        const storeId = document.getElementById("storeId").value;

        // 요청 데이터 생성
        const cardRegistrationData = {
            requestUniqueId: requestUniqueId,
            ci: ci,
            encryptedCardInfo: encryptedCardInfo,
            storeId: storeId,
        };

        // API 호출
        fetch("http://localhost:8443/api/cards/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(cardRegistrationData),
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then((data) => {
                console.log("Card registration successful:", data);
                alert(`카드정보 등록 성공\n식별자: ${data.cardIdentifier}\n요청고유번호: ${data.requestUniqueId}`);
                document.getElementById("cardIdentifier").value = data.cardIdentifier;
                fetchAndDisplayCards(); // 새로고침하여 데이터 업데이트
            })
            .catch((error) => {
                console.error("Error during card registration:", error);
                alert("카드등록 실패 :"+error);
            });
    });

    // 거래 요청
    document.getElementById("requestTransactionButton").addEventListener("click", function () {
        const cardIdentifier = document.getElementById("cardIdentifier").value;
        const orderNumber = document.getElementById("orderNumber").value;
        const storeId = document.getElementById("storeIdTransaction").value;
        const amount = document.getElementById("amount").value;
        const currency = document.getElementById("currency").value;
        const paymentType = document.getElementById("paymentType").value;
        const installmentMonths = document.getElementById("installmentMonths").value;

        // 요청 데이터 생성
        const transactionData = {
            cardIdentifier: cardIdentifier,
            orderNumber: orderNumber,
            storeId: storeId,
            amount: parseFloat(amount),
            currency: currency,
            paymentType: paymentType,
            installmentMonths: parseInt(installmentMonths),
        };

        // API 호출
        fetch("http://localhost:8443/api/transactions", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(transactionData),
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then((data) => {
                console.log("Transaction request successful:", data);
                alert(`거래처리 완료\nTransaction ID: ${data.transactionId}\nStatus: ${data.status}`);
                fetchAndDisplayTransactions(); // 거래 내역 갱신
            })
            .catch((error) => {
                console.error("Error during transaction request:", error);
                alert("Failed to process the transaction. Please try again.");
            });
    });



    // 오토필 데이터 생성기
    const generateUniqueData = () => {
        const timestamp = Date.now(); // 고유한 값 생성
        const generateRandomString = (length) => {
            const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            let result = "";
            for (let i = 0; i < length; i++) {
                result += chars.charAt(Math.floor(Math.random() * chars.length));
            }
            return result;
        };

        return {
            requestUniqueId: `REQ-${timestamp}`,
            ci: `${generateRandomString(88)}`,
            encryptedCardInfo: `${generateRandomString(90)}`,
            storeId: generateRandomString(4),
            orderNumber: `ORDER-${timestamp}`,
            amount: (Math.random() * 10000).toFixed(2),
            currency: "KRW",
        };
    };

    // 카드 등록 오토필
    document.getElementById("autoFillCardButton").addEventListener("click", function () {
        const data = generateUniqueData();
        document.getElementById("requestUniqueId").value = data.requestUniqueId;
        document.getElementById("ci").value = data.ci;
        document.getElementById("encryptedCardInfo").value = data.encryptedCardInfo;
        document.getElementById("storeId").value = data.storeId;
    });

    // 거래 요청 오토필
    document.getElementById("autoFillTransactionButton").addEventListener("click", function () {
        const data = generateUniqueData();
        document.getElementById("orderNumber").value = data.orderNumber;
        document.getElementById("storeIdTransaction").value = data.storeId;
        document.getElementById("amount").value = data.amount;
        document.getElementById("currency").value = data.currency;
        document.getElementById("paymentType").value = "CARD"; // 기본값 설정
        document.getElementById("installmentMonths").value = 0;
    });

    // 로그 갱신 함수
    const fetchLogs = (system, logAreaId) => {
        const url = systemUrls[system]; // 시스템별 URL 가져오기

        fetch(url)
            .then(response => response.text())
            .then(data => {
                const logArea = document.getElementById(logAreaId);
                const lines = data.split("\n");

                // 중복된 로그 필터링
                const newLines = lines.filter(line => !logsCache[system].has(line));
                newLines.forEach(line => logsCache[system].add(line));

                // 새로운 로그 추가
                if (newLines.length > 0) {
                    logArea.value += newLines.join("\n") + "\n";
                    logArea.scrollTop = logArea.scrollHeight; // 자동 스크롤
                }
            })
            .catch(error => console.error(`Failed to fetch logs for ${system}:`, error));
    };

    // 주기적으로 로그 갱신
    setInterval(() => {
        fetchLogs("pg", "pgLogArea");
        fetchLogs("token", "tokenLogArea");
        fetchLogs("van", "vanLogArea");
    }, 2000);
});
