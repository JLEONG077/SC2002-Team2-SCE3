# Restaurant Rush by johnPhone

## Repository Structure

```text
SC2002-Team2-SCE3/
└── diagrams                      # Folder containing diagrams
│   ├── happyhour_sequence.pdf    
│   ├── stage1_class.pdf        
│   ├── stage2_class.pdf
│   ├── stage3_class_v2.pdf
├── src/
│   ├── Main.java                 # Application entry point
│   ├── boundary/
│   │   └── ConsoleUI.java        # CLI interface, status headers, turn summary and result screen
│   ├── control/
│   │   ├── ArrivalSchedule.java  # Customer arrival sequence configuration
│   │   ├── GameConfig.java       # Base game parameters and constraints
│   │   ├── GameController.java   # Main game loop and execution controller
│   │   ├── Outcome.java          # Enum for Victory/Defeat states
│   │   ├── Restaurant.java       # Core simulation state, Happy Hour recovery, customer departures, served/departed counters
│   │   ├── ResultEvaluator.java  # Victory condition and objective evaluation
│   │   └── TurnProcessor.java    # Step-by-step turn execution workflow
│   └── entity/
│       ├── ActionResult.java     # Action execution response wrapper
│       ├── ActionType.java       # Enum for valid staff operations
│       ├── Cashier.java          # Payment processing entity
│       ├── Chef.java             # Kitchen preparation entity
│       ├── ComboMeal.java        # Composite menu item (10% combo discount)
│       ├── CriticCustomer.java   # Food critic polymorphic customer behavior
│       ├── Customer.java         # Abstract base class for customers (tracks served turn, awaiting-service check)
│       ├── CustomerState.java    # Enum for customer lifecycle states
│       ├── HappyHourPricing.java # Pricing policy: 20% off during Happy Hour
│       ├── Host.java             # Seating assistance staff role
│       ├── MenuItem.java         # Menu item properties and pricing
│       ├── Order.java            # Order tracking and preparation state
│       ├── OrderStatus.java      # Enum for order progress (PLACED, PREPARING, READY, SERVED, PAID, CANCELLED)
│       ├── PricingPolicy.java    # Pricing policy interface
│       ├── RegularCustomer.java  # Standard customer behavior profile
│       ├── Staff.java            # Abstract base class for staff (declares abstract perform())
│       ├── StaffAction.java      # Encapsulated staff task parameters
│       ├── StandardPricing.java  # Default pricing policy
│       ├── Table.java            # Dining table occupancy state
│       ├── VIPCustomer.java      # VIP polymorphic customer profile (10% discount)
│       └── Waiter.java           # Waiter service: taking orders and serving
├── bin/                          # Compiled class files (auto-generated)
├── .gitignore
└── README.md                     # System documentation
```

---

## Gameplay Overview

### Turn Flow
Each turn runs in the following order:

1. **Start of turn:** the pricing policy updates and new customers arrive. If Happy Hour is still available, the Manager is asked whether to activate it. Happy Hour recovery is then applied (see below).
2. **Status header:** shows the turn number, current revenue, average satisfaction of paid customers, and Happy Hour state.
3. **Host:** seats at most one queued customer. Invalid choices are rejected and the Host is prompted again.
4. **Waiter:** takes an order or serves a ready dish. The Waiter acts before the Chef, so a dish that finishes cooking this turn cannot be served until the next turn.
5. **Chef:** advances an order in preparation. An ID with no order in preparation is rejected with an error.
6. **Eating and payment:** the Cashier collects payment from a customer who was already ready to pay at the start of this step. Customers served on an earlier turn then become ready to pay, so they pay on the next turn at the earliest.
7. **Waiting loss:** customers who have not yet been served lose satisfaction. At 0 satisfaction they leave unhappy, their order is cancelled, they leave the queue and their table is freed.
8. **Turn summary:** shows served, paid and unhappy-departure counts, revenue, average satisfaction and turns used.

### Happy Hour
* Can be activated once per game by the Manager.
* Gives 20% off new orders for the turn it is activated and the next.
* Customers still waiting for a table or food recover 15 satisfaction (max 100) at the start of each active turn.

### Pricing
* Prices are stored in cents and discounts use integer rounding (round half up) for deterministic results: Happy Hour 20% off, VIP 10% off, Combo Meal 10% off.

### Victory Conditions
The game ends in victory if, at game over, all of the following hold:
* At least **4** customers have paid
* Revenue is at least **$45.00**
* Average satisfaction of paid customers is at least **60**

The result screen prints each of these values against its target.

---

## Installation & CLI Execution Instructions

### Prerequisites
* Java Development Kit (JDK) 11 or higher installed and added to system PATH.

### 1. Clone the Repository
Open your terminal and clone the repository to your local machine:
```bash
git clone https://github.com/JLEONG077/SC2002-Team2-SCE3.git
cd SC2002-Team2-SCE3
```

### 2. Compile the Source Code
Navigate to the root directory and compile all Java source files into the output directory (`bin`):
```bash
javac -d bin src/Main.java src/boundary/*.java src/control/*.java src/entity/*.java
```

### 3. Run the Application
```bash
java -cp bin Main
```
Execute the compiled program from the command line interface:
```bash
java -cp bin Main
```
