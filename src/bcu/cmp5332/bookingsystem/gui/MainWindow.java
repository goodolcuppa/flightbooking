package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;

public class MainWindow extends JFrame implements ActionListener {

    private JMenuBar menuBar;
    private JMenu adminMenu;
    private JMenu flightsMenu;
    private JMenu bookingsMenu;
    private JMenu customersMenu;

    private JMenuItem adminExit;

    private JMenuItem flightsView;
    private JMenuItem flightsAdd;
    private JMenuItem flightsDel;
    private JMenuItem flightsViewPassengers;
    
    private JMenuItem bookingsIssue;
    private JMenuItem bookingsUpdate;
    private JMenuItem bookingsCancel;

    private JMenuItem custView;
    private JMenuItem custAdd;
    private JMenuItem custDel;
    private JMenuItem custViewBookings;

    private FlightBookingSystem fbs;

    public MainWindow(FlightBookingSystem fbs) {

        initialize();
        this.fbs = fbs;
    }
    
    public FlightBookingSystem getFlightBookingSystem() {
        return fbs;
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {

        }

        setTitle("Flight Booking Management System");

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        //adding adminMenu menu and menu items
        adminMenu = new JMenu("Admin");
        menuBar.add(adminMenu);

        adminExit = new JMenuItem("Exit");
        adminMenu.add(adminExit);
        adminExit.addActionListener(this);

        // adding Flights menu and menu items
        flightsMenu = new JMenu("Flights");
        menuBar.add(flightsMenu);

        flightsView = new JMenuItem("View");
        flightsAdd = new JMenuItem("Add");
        flightsDel = new JMenuItem("Delete");
        flightsViewPassengers = new JMenuItem("View Passengers");
        
        flightsMenu.add(flightsView);
        flightsMenu.add(flightsAdd);
        flightsMenu.add(flightsDel);
        flightsMenu.add(flightsViewPassengers);
        // adding action listener for Flights menu items
        for (int i = 0; i < flightsMenu.getItemCount(); i++) {
            flightsMenu.getItem(i).addActionListener(this);
        }
        
        // adding Bookings menu and menu items
        bookingsMenu = new JMenu("Bookings");
        menuBar.add(bookingsMenu);
        
        bookingsIssue = new JMenuItem("Issue");
        bookingsUpdate = new JMenuItem("Update");
        bookingsCancel = new JMenuItem("Cancel");
        
        bookingsMenu.add(bookingsIssue);
        bookingsMenu.add(bookingsUpdate);
        bookingsMenu.add(bookingsCancel);
        // adding action listener for Bookings menu items
        for (int i = 0; i < bookingsMenu.getItemCount(); i++) {
            bookingsMenu.getItem(i).addActionListener(this);
        }

        // adding Customers menu and menu items
        customersMenu = new JMenu("Customers");
        menuBar.add(customersMenu);

        custView = new JMenuItem("View");
        custAdd = new JMenuItem("Add");
        custDel = new JMenuItem("Delete");
        custViewBookings = new JMenuItem("View Bookings");

        customersMenu.add(custView);
        customersMenu.add(custAdd);
        customersMenu.add(custDel);
        customersMenu.add(custViewBookings);
        // adding action listener for Customers menu items
        custView.addActionListener(this);
        custAdd.addActionListener(this);
        custDel.addActionListener(this);
        custViewBookings.addActionListener(this);

        setSize(800, 500);

        setVisible(true);
        setAutoRequestFocus(true);
        toFront();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
/* Uncomment the following line to not terminate the console app when the window is closed */
        //setDefaultCloseOperation(DISPOSE_ON_CLOSE);        

    }	

/* Uncomment the following code to run the GUI version directly from the IDE */
    public static void main(String[] args) throws IOException, FlightBookingSystemException {
        FlightBookingSystem fbs = FlightBookingSystemData.load();
        new MainWindow(fbs);			
    }



    @Override
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == adminExit) {
            try {
                FlightBookingSystemData.store(fbs);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0);
        } else if (ae.getSource() == flightsView) {
            displayFlights();
            
        } else if (ae.getSource() == flightsAdd) {
            new AddFlightWindow(this);
            
        } else if (ae.getSource() == flightsDel) {
            new DelFlightWindow(this);
            
        } else if (ae.getSource() == flightsViewPassengers) {
        	new ShowPassengersWindow(this);
        	
        } else if (ae.getSource() == bookingsIssue) {
            new IssueBookingWindow(this);
            
        } else if (ae.getSource() == bookingsCancel) {
            new CancelBookingWindow(this);
            
        } else if (ae.getSource() == bookingsUpdate) {
        	new UpdateBookingWindow(this);
        } 
        else if (ae.getSource() == custView) {
            displayCustomers();
            
        } else if (ae.getSource() == custAdd) {
            new AddCustomerWindow(this);
            
        } else if (ae.getSource() == custDel) {
            new DelCustomerWindow(this);
            
        } else if (ae.getSource() == custViewBookings) {
        	new ShowBookingsWindow(this);
        	
        }
    }

    public void displayFlights() {
        List<Flight> flightsList = fbs.getFlights();
        // headers for the table
        String[] columns = new String[]{"Flight No", "Origin", "Destination", "Departure Date", "Capacity", "Price"};
        LocalDate current = LocalDate.now();

        Object[][] data = new Object[flightsList.size()][6];
        for (int i = 0; i < flightsList.size(); i++) {
            Flight flight = flightsList.get(i);
            if(flight.getVisibility() == true && flight.getDepartureDate().isAfter(current)) {
                data[i][0] = flight.getFlightNumber();
                data[i][1] = flight.getOrigin();
                data[i][2] = flight.getDestination();
                data[i][3] = flight.getDepartureDate();
                data[i][4] = flight.getCapacity();
                data[i][5] = flight.getPrice();
            }

        }

        JTable table = new JTable(data, columns);
        this.getContentPane().removeAll();
        this.getContentPane().add(new JScrollPane(table));
        this.revalidate();
    }	
    
    public void displayCustomers() {
    	List<Customer> customersList = fbs.getCustomers();
    	
    	String[] columns = new String[] {"Name", "Phone", "Email", "Bookings"};
    	
    	Object[][] data = new Object[customersList.size()][6];
    	for (int i = 0; i < customersList.size(); i++) {
            Customer customer = customersList.get(i);
            if(customer.getVisible() == true) {
                data[i][0] = customer.getName();
                data[i][1] = customer.getPhone();
                data[i][2] = customer.getEmail();
                data[i][3] = customer.getBookings().size();
            }
        }
    	
    	JTable table = new JTable(data, columns);
        this.getContentPane().removeAll();
        this.getContentPane().add(new JScrollPane(table));
        this.revalidate();
    }
    
    public void showFlight(Flight flight) {
        List<Customer> passengers = flight.getPassengers();

        String[] columns = new String[] {"ID", "Name"};

        Object[][] data = new Object[passengers.size()][6];
        for (int i = 0; i < passengers.size(); i++) {
            Customer customer = passengers.get(i);
            data[i][0] = customer.getID();
            data[i][1] = customer.getName();
        }

        JTable table = new JTable(data, columns);
        this.getContentPane().removeAll();
        this.getContentPane().add(new JLabel(flight.getFlightNumber()));
        this.getContentPane().add(new JScrollPane(table));
        this.revalidate();
    }

    public void showCustomer(Customer customer) {
        List<Booking> bookings = customer.getBookings();
        
        String[] columns = new String[] {"Flight No", "Origin", "Destination", "Price", "Status"};
        
        Object[][] data = new Object[bookings.size()][6];
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            data[i][0] = booking.getFlight().getFlightNumber();
            data[i][1] = booking.getFlight().getOrigin();
            data[i][2] = booking.getFlight().getDestination();
            data[i][3] = booking.getPrice();
            data[i][4] = booking.getStatus();
        }
        
        JTable table = new JTable(data, columns);
        this.getContentPane().removeAll();
        this.getContentPane().add(new JLabel(customer.getName()));
        this.getContentPane().add(new JScrollPane(table));
        this.revalidate();
    }
}
