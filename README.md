# Campus-Exchange🎓🛒

> A P2P marketplace exclusively for the any college community.

## 📖 Overview

Campus-Exchange is hyperlocal marketplace exclusively for students in a college community.

## 💡 Motivation
Our main motivation was to fix the idea of selling products over in WhatsApp groups. So we decided to make an independent centralized application where items can be traded with trust and ease.

## ✨ Key Features

- **Verified Access:** Users have to register themselves as part of the college community and select their college then register themselves andbe part of the community.
- **Login via token**: Users are given a random token upon every login visit which has a fixed duration of expiry after which (s)he has to login again.
- **OTP Verification**: OTP verification sent over e-mail registered by the users with expiry of 2 minutes.
- **Item Listing**: Items can be listed by the users with price, quantity and image.
- **Item Delisting**: Items are automatically delisted after 1 year if not sold.
- **Blocked User**: Some uesrs can be blocked by the item lister if the deal is not successful even after them claiming the items
- **Image Uploading:** Images can be uploaded using multi-path
- **Filter Feature:** Items can be filtered based on the categories provided.
- **Notification Updates:** Claimers and Listers are provided with timely notifications
- **Claim Item**: Items listed can be claimed prior to which lister receives the notification of the claim
- **LogFile Generation**: Every activity of the user is tracked and listed in a LogFile which can be used for security purposes.

## 🔌 Endpoints Description

### 🛡️ Authentication Controller
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/signup-request` | **Signup**: Initiates registration by accepting user details and delegating logic to AuthService. |
| `POST` | `/api/auth/verify-otp` | **Verify OTP**: Validates the One-Time Password to complete registration and create the user. |
| `POST` | `/api/auth/login` | **Login**: Authenticates user via email/password and returns session data. |
| `POST` | `/api/auth/logout` | **Logout**: Invalidates the current session token. |

### 📦 Item Controller
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/items` | **Create Item**: Uploads an image and details to list a new item (Multipart request). |
| `GET` | `/api/items` | **Get Items**: Fetches all items, with optional filtering by `category` and `college`. |
| `GET` | `/api/items/{id}` | **Get Item by ID**: Retrieves detailed information for a specific item. |
| `GET` | `/api/items/listed` | **Get Listed Items**: Displays all items currently listed by the logged-in user. |

### 🤝 Claim Controller
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/claims` | **Create Claim**: Allows a buyer to claim an item using the `ClaimRequest` body. |
| `PUT` | `/api/claims/accept` | **Accept Claim**: Allows the lister (seller) to accept a specific claim request. |
| `PUT` | `/api/claims/reject` | **Reject Claim**: Allows the lister to reject a claim request. |
| `PUT` | `/api/claims/relist` | **Relist Item**: Puts an item back on the market after a failed deal and blocks the claimer. |
| `PUT` | `/api/claims/item/{id}/complete`| **Complete Deal**: Marks a transaction as successfully completed. |

### 🔔 Notification Controller
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/notifications/{userId}` | **Get Notifications**: Retrieves alerts for a specific user after verifying their session. |

## 🛠️ Tech Stack

- **Backend:** Spring Framework
- **Database:** JSON
- **Authentication:** OAuth

## 🚀 Installation & Setup

1.  **Clone the repository**

    ```bash
    cd java-app
    git clone https://github.com/Taneesh-Bhojawala/Java-Project-SEM-3.git
    ```
2. **Check if Java is installed:**
    ```bash
    java --version
    ```
3. **If not installed:**
    ```bash
    winget install -e --id Oracle.JDK.25
    ```
2.  **Run the Application**
    ```bash
    java -jar App.jar
    ```

## 🔮 Future Scope

- **Front End**
- **Web Deployment**
- **Chat Feature and Payment Gateway**

## 🤝 Contributing

- Taneesh Bhojawala(BT2024053)
- Aryan Khadgi(BT2024151)
- Abhinav Bhatia(BT2024156)
- Omkumar Alpeshbhai Aghera(BT2024088)

## 📜 License
Distributed under the MIT License. See [LICENSE](https://github.com/Taneesh-Bhojawala/Java-Project-SEM-3/blob/main/LICENSE) for more information.
