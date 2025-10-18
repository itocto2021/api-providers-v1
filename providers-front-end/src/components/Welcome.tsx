import React, { useState, useEffect } from 'react';
import {
  Box,
  Container,
  Typography,
  Paper,
  Avatar,
  CircularProgress,
  Alert,
  Fade,
  Card,
  CardContent,
} from '@mui/material';
import { styled } from '@mui/material/styles';
import { Info as InfoIcon } from '@mui/icons-material';
import { apiService } from '../services/apiService';
import { AppInfo } from '../types';
import candidatoImage from '../assets/candidato.png';

const WelcomeContainer = styled(Container)(({ theme }) => ({
  paddingTop: theme.spacing(4),
  paddingBottom: theme.spacing(4),
}));

const WelcomeCard = styled(Card)(({ theme }) => ({
  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  color: 'white',
  borderRadius: theme.spacing(2),
  boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)',
  marginBottom: theme.spacing(3),
}));

const CandidateAvatar = styled(Avatar)(({ theme }) => ({
  width: theme.spacing(15),
  height: theme.spacing(16),
  backgroundColor: 'rgba(255, 255, 255, 0.9)',
  color: theme.palette.primary.main,
  fontSize: theme.spacing(6),
  margin: '0 auto',
  marginBottom: theme.spacing(2),
}));

const InfoCard = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(3),
  textAlign: 'center',
  borderRadius: theme.spacing(2),
  boxShadow: '0 4px 16px rgba(0, 0, 0, 0.1)',
}));

const Welcome: React.FC = () => {
  const [appInfo, setAppInfo] = useState<AppInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchAppInfo = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await apiService.getAppInfo();
        setAppInfo(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Error al cargar información');
        console.error('Error fetching app info:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchAppInfo();
  }, []);

  if (loading) {
    return (
      <WelcomeContainer maxWidth="md">
        <Box 
          display="flex" 
          justifyContent="center" 
          alignItems="center" 
          minHeight="400px"
        >
          <CircularProgress size={60} />
        </Box>
      </WelcomeContainer>
    );
  }

  if (error) {
    return (
      <WelcomeContainer maxWidth="md">
        <Alert 
          severity="error" 
          sx={{ borderRadius: 2 }}
        >
          {error}
        </Alert>
      </WelcomeContainer>
    );
  }

  return (
    <WelcomeContainer maxWidth="md">
      <Fade in={true} timeout={1000}>
        <WelcomeCard>
          <CardContent sx={{ textAlign: 'center', py: 4 }}>
            {/* Imagen del candidato */}
            <CandidateAvatar src={candidatoImage} alt="Candidato" />
            
            
            {/* Mensaje de bienvenida desde la API */}
            <Typography 
              variant="h4" 
              component="h1" 
              gutterBottom
              sx={{ 
                fontWeight: 700,
                textShadow: '0 2px 4px rgba(0,0,0,0.3)' 
              }}
            >
              {appInfo?.mensaje || 'Bienvenido Candidato'}
            </Typography>
            
            <Typography 
              variant="h6" 
              sx={{ 
                opacity: 0.9,
                fontWeight: 300 
              }}
            >
              Sistema de Gestión de Proveedores
            </Typography>
          </CardContent>
        </WelcomeCard>
      </Fade>

      <Fade in={true} timeout={1500}>
        <InfoCard>
          <Box display="flex" alignItems="center" justifyContent="center" mb={2}>
            <InfoIcon color="primary" sx={{ mr: 1 }} />
            <Typography variant="h6" color="primary" fontWeight={600}>
              Información de la Aplicación
            </Typography>
          </Box>
          
          <Typography variant="body1" color="text.secondary" gutterBottom>
            Versión: <strong>{appInfo?.version || '1.0.0'}</strong>
          </Typography>
          
          <Typography variant="body2" color="text.secondary">
            e-Commerce Gapsi - Sistema de Proveedores
          </Typography>
        </InfoCard>
      </Fade>
    </WelcomeContainer>
  );
};

export default Welcome;