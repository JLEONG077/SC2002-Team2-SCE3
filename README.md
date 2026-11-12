# Restaurant Rush by johnPhone

## Repository Structure

```text
SC2002-Team2-SCE3/
├── src/
│   ├── Main.java               # Application entry point
│   ├── boundary/
│   │   └── ConsoleUI.java      # CLI interface and user prompt rendering
│   ├── control/
│   │   ├── ArrivalSchedule.java  # Customer arrival sequence configuration
│   │   ├── GameConfig.java     # Base game parameters and constraints
│   │   ├── GameController.java # Main game loop and execution controller
│   │   ├── Outcome.java        # Enum for Victory/Defeat states
│   │   ├── Restaurant.java     # Core simulation state and business logic
│   │   ├── ResultEvaluator.java# Victory condition and objective evaluation
│   │   └── TurnProcessor.java  # Step-by-step turn execution workflow
│   └── entity/
│       ├── ActionResult.java   # Action execution response wrapper
│       ├── ActionType.java     # Enum for valid staff operations
│       ├── Cashier.java        # Payment processing entity
│       ├── Chef.java           # Kitchen preparation entity
│       ├── ComboMeal.java      # Composite menu item implementation
│       ├── CriticCustomer.java # Food critic polymorphic customer behavior
│       ├── Customer.java       # Abstract base class for customer behaviors
│       ├── CustomerState.java  # Enum for customer lifecycle states
│       ├── Host.java           # Seating assistance staff role
│       ├── MenuItem.java       # Menu item properties and pricing
│       ├── Order.java          # Order tracking and preparation state
│       ├── OrderStatus.java    # Enum for kitchen preparation progress
│       ├── RegularCustomer.java# Standard customer behavior profile
│       ├── Staff.java          # Abstract base class for staff members
│       ├── StaffAction.java    # Encapsulated staff task parameters
│       ├── Table.java          # Dining table occupancy state
│       ├── VIPCustomer.java    # VIP polymorphic customer profile
│       └── Waiter.java         # Waiter service and seating management
├── bin/                        # Compiled class files (auto-generated)
└── README.md                   # System documentation
```

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
Execute the compiled program from the command line interface:
```bash
java -cp bin Main
```
