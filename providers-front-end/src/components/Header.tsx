import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { AppBar, Toolbar, Typography, Box, Button, Tabs, Tab } from '@mui/material';
import { styled } from '@mui/material/styles';
import { Home as HomeIcon, Business as BusinessIcon } from '@mui/icons-material';
import logoGapsi from '../assets/logo-gapsi.png';

const StyledAppBar = styled(AppBar)(({ theme }) => ({
  backgroundColor: '#1976d2',
  boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
}));

const LogoContainer = styled(Box)({
  display: 'flex',
  alignItems: 'center',
  gap: '12px',
  cursor: 'pointer',
});

const LogoText = styled(Typography)(({ theme }) => ({
  fontWeight: 700,
  fontSize: '1.5rem',
  color: 'white',
  letterSpacing: '0.5px',
}));

const NavigationTabs = styled(Tabs)(({ theme }) => ({
  marginLeft: theme.spacing(3),
  '& .MuiTab-root': {
    color: 'rgba(255, 255, 255, 0.7)',
    fontWeight: 600,
    textTransform: 'none',
    minHeight: 48,
    '&.Mui-selected': {
      color: 'white',
    },
  },
  '& .MuiTabs-indicator': {
    backgroundColor: 'white',
  },
}));

const GapsiLogo: React.FC<{ onClick?: () => void }> = ({ onClick }) => (
  <LogoContainer onClick={onClick}>
    <Box
      component="img"
      src={logoGapsi}
      alt="Logo Gapsi"
      sx={{
        width: 80,
        height: 40,
        objectFit: 'contain',
        backgroundColor: 'white',
        borderRadius: '8px',
        padding: '4px',
      }}
    />
    <LogoText variant="h6">
      e-Commerce Gapsi 
    </LogoText>
  </LogoContainer>
);

interface HeaderProps {
  title?: string;
}

const Header: React.FC<HeaderProps> = ({ title }) => {
  const navigate = useNavigate();
  const location = useLocation();

  const getActiveTab = () => {
    if (location.pathname === '/welcome') return 0;
    if (location.pathname === '/providers') return 1;
    return 0;
  };

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    switch (newValue) {
      case 0:
        navigate('/welcome');
        break;
      case 1:
        navigate('/providers');
        break;
      default:
        navigate('/welcome');
    }
  };

  const handleLogoClick = () => {
    navigate('/welcome');
  };

  return (
    <StyledAppBar position="static">
      <Toolbar>
        <GapsiLogo onClick={handleLogoClick} />
        
        <NavigationTabs
          value={getActiveTab()}
          onChange={handleTabChange}
          aria-label="navegación principal"
        >
          <Tab 
            icon={<HomeIcon />} 
            label="Inicio" 
            iconPosition="start"
          />
          <Tab 
            icon={<BusinessIcon />} 
            label="Proveedores" 
            iconPosition="start"
          />
        </NavigationTabs>
        {title && (
          <Typography 
            variant="h6" 
            component="div" 
            sx={{ 
              flexGrow: 1, 
              ml: 3,
              fontWeight: 500 
            }}
          >
            {title}
          </Typography>
        )}
        <Box sx={{ flexGrow: 1 }} />
      </Toolbar>
    </StyledAppBar>
  );
};

export default Header;