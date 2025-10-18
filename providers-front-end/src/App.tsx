import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { CssBaseline, Box } from '@mui/material';
import Header from './components/Header';
import Welcome from './components/Welcome';
import ProvidersList from './components/ProvidersList';
import ProviderForm from './components/ProviderForm';
import { Provider } from './types';
import './styles/global.css';

// Tema personalizado con colores de Gapsi
const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
      dark: '#115293',
      light: '#42a5f5',
    },
    secondary: {
      main: '#dc004e',
    },
    background: {
      default: '#f5f5f5',
    },
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h4: {
      fontWeight: 700,
    },
    h6: {
      fontWeight: 600,
    },
  },
  shape: {
    borderRadius: 12,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 600,
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        },
      },
    },
  },
});

// Componente principal de la aplicación
const App: React.FC = () => {
  const [formOpen, setFormOpen] = useState(false);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const handleAddProvider = () => {
    setFormOpen(true);
  };

  const handleFormClose = () => {
    setFormOpen(false);
  };

  const handleProviderCreated = (provider: Provider) => {
    console.log('Proveedor creado:', provider);
    // Trigger refresh de la lista
    setRefreshTrigger(prev => prev + 1);
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <Box sx={{ minHeight: '100vh', backgroundColor: 'background.default' }}>
          <Header />
          
          <Routes>
            {/* Ruta de bienvenida */}
            <Route 
              path="/welcome" 
              element={<Welcome />} 
            />
            
            {/* Ruta de gestión de proveedores */}
            <Route 
              path="/providers" 
              element={
                <ProvidersList 
                  onAddProvider={handleAddProvider}
                  refreshTrigger={refreshTrigger}
                />
              } 
            />
            
            {/* Redirección por defecto a bienvenida */}
            <Route 
              path="/" 
              element={<Navigate to="/welcome" replace />} 
            />
            
            {/* Ruta 404 - redirige a bienvenida */}
            <Route 
              path="*" 
              element={<Navigate to="/welcome" replace />} 
            />
          </Routes>

          {/* Formulario modal para agregar proveedor */}
          <ProviderForm
            open={formOpen}
            onClose={handleFormClose}
            onSuccess={handleProviderCreated}
          />
        </Box>
      </Router>
    </ThemeProvider>
  );
};

export default App;